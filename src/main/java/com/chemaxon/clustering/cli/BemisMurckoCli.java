/*
 * Copyright 2018 ChemAxon Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.chemaxon.clustering.cli;

import chemaxon.clustering.adapter.LegacyLibraryMcsAdapter;
import chemaxon.clustering.adapter.LegacyLibraryMcsParameters;
import chemaxon.struc.Molecule;
import com.chemaxon.calculations.common.SubProgressObserver;
import com.chemaxon.calculations.io.CloseableIterator;
import com.chemaxon.calculations.io.MoleculeIo;
import com.chemaxon.calculations.io.OrderedInputProcessingException;
import com.chemaxon.calculations.io.SmilesMemoizingMoleculeIterator;
import com.chemaxon.calculations.util.CmdlineUtils;
import com.chemaxon.clustering.common.DissimilarityInput;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.chemaxon.clustering.common.MolInput;
import com.chemaxon.clustering.common.MolInputBuilder;
import com.chemaxon.clustering.framework.BemisMurckoClustering;
import com.chemaxon.clustering.framework.FrameworkClusteringResults;
import com.chemaxon.clustering.framework.MoleculeFrameworks;
import com.chemaxon.clustering.util.Util;
import com.chemaxon.descriptors.fingerprints.cfp.CfpComparator;
import com.chemaxon.descriptors.fingerprints.cfp.CfpGenerator;
import com.chemaxon.descriptors.fingerprints.cfp.CfpParameters;
import com.chemaxon.descriptors.metrics.BinaryMetrics;
import com.chemaxon.overlap.cli.invocation.CliInvocation;
import com.chemaxon.overlap.cli.invocation.CliInvocationEnv2;
import com.chemaxon.overlap.cli.util.images.DrawMoleculeToRenderer;
import com.chemaxon.overlap.cli.util.images.Halign;
import static com.chemaxon.overlap.cli.util.images.Px2d.of;
import com.chemaxon.overlap.cli.util.images.Valign;
import com.chemaxon.overlap.io.StandardizerWrappers;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Diagnostic CLI for some clustering and related API functionality.
 *
 * @author Gabor Imre
 */
public final class BemisMurckoCli {



    /**
     * No constructor exposed.
     */
    private BemisMurckoCli() {}

    /**
     * Read input through.
     *
     * Useful for benchmark the baseline import speed.
     *
     * @param mi Input to read
     * @param po Observer to track progress. Method {@link SubProgressObserver#done()} will be invoked. One work unit is
     * associated to one structure read.
     * @return processed structures count
     */
    private static int justread(Iterator<Molecule> mi, SubProgressObserver po) {
        int count = 0;
        try {
            while (mi.hasNext()) {
                mi.next();
                po.worked(1);
                count ++;
            }
        } finally {
            po.done();
        }
        return count;
    }

    /**
     * Read input and write back them as SMILES.
     *
     * Useful for benchmark the baseline import/export speed. Also an example of a basic error handling concept.
     *
     * @param mi Structure sources
     * @param errorHandler Error handler for failed exports. Errors expected during structure conversion, before
     * printing to the output. In case of an expected error the passed handlers {@link Consumer#accept(java.lang.Object)}
     * method will be invoked with an {@link OrderedInputProcessingException}. It is up to the handler to throw a
     * {@link RuntimeException} which aborts the processing or log/administrate/ignore the error.
     * @param exporter Exporter to use
     * @param out Output to write non failed structures
     * @param po Observer to track progress. Method {@link SubProgressObserver#done()} will be invoked. One work unit
     * is associated to one structure read
     * @return Input count
     */
    private static int readwrite(
            Iterator<Molecule> mi,
            Consumer<Exception> errorHandler,
            Function<Molecule, String> exporter,
            PrintStream out,
            SubProgressObserver po) {
        try {
            int inputIndex = 0;
            while (mi.hasNext()) {
                final Molecule m = mi.next();

                try {
                    out.println(exporter.apply(m));
                } catch (IllegalArgumentException e) {
                    errorHandler.accept(new OrderedInputProcessingException(inputIndex, e));
                }
                inputIndex ++;
                po.worked(1);
            }
            return inputIndex;
        } finally {
            po.done();
        }

    }

    /**
     * Append bemis-murcko frameworks to imported structures.
     *
     * @param mi Structure source
     * @param errorHandler Error handler. Errors expected during framework calculation and input structure conversion.
     * @param out Target to write
     * @param po Observer to track progress. Method {@link SubProgressObserver#done()} will be invoked. One work unit
     * is associated to one input structure read.
     * @return Input count
     */

    private static int bmf(Iterator<Molecule> mi, Consumer<Exception> errorHandler, PrintStream out, SubProgressObserver po) {
        // Converter to convert read structure to SMILES
        final Function<Molecule, String> toSmiles = MoleculeIo.molExporterToSmilesFunction();

        // Framework association
        final Function<Molecule, Iterator<String>> bmf = new MoleculeFrameworks().bemisMurckoFrameworkAssociation()::apply;
        try {
            int inputIndex = 0;
            while (mi.hasNext()) {
                final Molecule m = mi.next();
                try {
                    // We expect error during converting input structure and during framework enumeration
                    // Collect outputs to be written and write only wen no error occurred
                    List<String> outlines = new ArrayList<String>();

                    // Will write original input structure
                    outlines.add(toSmiles.apply(m));

                    // Iterate frameworks
                    final Iterator<String> frameworks = bmf.apply(m);
                    while (frameworks.hasNext()) {
                        // And add them to the collection
                        outlines.add(frameworks.next());
                    }

                    // This point is reached when no error occurred
                    for(String s : outlines) {
                        out.println(s);
                    }
                } catch (IllegalArgumentException e) {
                    errorHandler.accept(new OrderedInputProcessingException(inputIndex, e));
                }
                po.worked(1);
                inputIndex ++;
            }
            return inputIndex;
        } finally {
            po.done();
        }
    }



    /**
     * Print tree to structures.
     *
     * All structures (leaves) of the tree will be printed as a SMILES string following by the frameworks of the
     * clusters leading to a root.
     *
     * @param hierarchy Clustering hierarchy for traversal
     * @param clusterIdToFrameworkSmiles Source of frameworks as SMILES
     * @param structureIdToStructureSmiles Source of input structures as SMILES
     * @param out Target to write to
     * @param po Observer to track progress. Method {@link SubProgressObserver#done()} will be invoked. One work unit is
     * associated to one leave (input structure) printed.
     */
    private static void bmtreemol_out(
            final IDBasedHierarchicClustering hierarchy,
            final Function<Integer, String> clusterIdToFrameworkSmiles,
            final Function<Integer, String> structureIdToStructureSmiles,
            final PrintStream out,
            final SubProgressObserver po) {

        Util.traverseSimplePostOrderDfs( // This traversal will wisit all clusters with DFS
                hierarchy, // Clustering to be traversed
                (path, visited) -> { // Function to invoked on each cluster
                    // we have a DFS traversal path
                    // visit last element (the cluster)
                    if (!visited.leaves().isEmpty()) {
                        // visit immediate children
                        for (int ci : visited.leaves()) {
                            for (int j = 0; j < path.size(); j++) {
                                final int cjid = path.get(j).getClusterID();
                                out.println(clusterIdToFrameworkSmiles.apply(cjid) + " CID: " + cjid + " Depth: " + j);
                            }
                            out.println(structureIdToStructureSmiles.apply(ci));
                        }
                        po.worked(visited.leaves().size());
                    }
                });
        po.done();
    }

    /**
     * Image output.
     *
     * @param tree Results tree to traverse
     * @param leafIdToStructure Structure look up for lead ids
     * @param out Target to write to
     * @param po Observer to track progress. Method {@link SubProgressObserver#done()} will be invoked.
     */
    private static void bmtreeimg_out(
            final FrameworkClusteringResults tree,
            final Function<Integer, Molecule> leafIdToStructure,
            final OutputStream out,
            final SubProgressObserver po) throws IOException {

        final DrawMoleculeToRenderer drawBorderedMolecule =
                new DrawMoleculeToRenderer().border("#CCCCCC", 0).shrinkMoleculeArea(1).labelColor("#AAAAAA");

        final DrawMoleculeToRenderer drawNoBorderedMolecule =
                new DrawMoleculeToRenderer().labelColor("#AAAAAA");


        new DetailedClusteringRendering(tree.getHierarchy())
                .clusterImageSize(of(70, 70))
                .clusterImage((cluster, renderer, area) -> {
                    final Molecule frameworkMolecule = tree.getFrameworkAsMolecule(cluster.getClusterID());
                    drawBorderedMolecule.paint(
                            renderer,
                            area,
                            frameworkMolecule,
                            "CID: " + cluster.getClusterID());
                })
                .leafImageSize(of(100, 100))
                .leafMaxCols(10)
                .leafImage((leafid, renderer, area) -> {
                    final Molecule leafMolecule = leafIdToStructure.apply(leafid);
                    drawNoBorderedMolecule.paint(
                            renderer,
                            area,
                            leafMolecule,
                            leafMolecule.getName()
                    );
                })
                .leafSeparation(0)
                .leafGroupSeparation(5)
                .writeToPngImage(out, po);
    }



    static void launch(BemisMurckoCliParameters params, CliInvocationEnv2<BemisMurckoCliStat> env) throws Exception {
        // Initialize execution input, output and progress observing then dispatch execution
        // Expect progress observer to be closed by the invoked method
        final SubProgressObserver po = env.progressObserver("Importing", (stat, timer) -> stat.timestatImport = timer);

        try (
                final CloseableIterator<Molecule> mi =
                        MoleculeIo.molImporterIterateMolecules(CmdlineUtils.inputStreamFromLocation(params.in));
                final PrintStream out = CmdlineUtils.printStreamFromLocation(params.out);
        ) {
            switch (params.mode) {
                case READMOLS: {
                    final int count = justread(mi, po);
                    env.whenStat(t -> t.targetCount = count);
                    break;
                }
                case READWRITE: {
                    final int count = readwrite(mi, params.errorHandling.getSuitableErrorHandler(), MoleculeIo.molExporterToSmilesFunction(), out, po);
                    env.whenStat(t -> t.targetCount = count);
                    break;
                }
                case READWRITECANONIC: {
                    final int count = readwrite(mi, params.errorHandling.getSuitableErrorHandler(), new MoleculeFrameworks().canonicalSmiles()::apply, out, po);
                    env.whenStat(t -> t.targetCount = count);
                    break;
                }
                case BMF: {
                    final int count = bmf(mi, params.errorHandling.getSuitableErrorHandler(), out, po);
                    env.whenStat(t -> t.targetCount = count);
                    break;
                }
                case BMTREEMOL : {
                    // Create clustering and traverse to print out hierarchy in a struture file
                    // Iterate inputs
                    final SmilesMemoizingMoleculeIterator sci =
                            MoleculeIo.molImporterMemoizingIterator(mi, params.errorHandling.getSuitableErrorHandler());

                    // Launch clustering
                    final FrameworkClusteringResults tree = BemisMurckoClustering
                            .ofOrderedMoleculesIterator(sci)
                            .withErrorHandler(params.errorHandling.getSuitableErrorHandler())
                            .launch(po);

                    env.whenStat(t -> t.targetCount = sci.size());

                    // Traverse and write output
                    final SubProgressObserver outpo = env.progressObserver("Output", (stat, timer) -> stat.timestatExport = timer);

                    bmtreemol_out(
                            tree.getHierarchy(),
                            tree.getFrameworkAsSmilesFunction()::apply,
                            sci.getMemoizedFunction(),
                            out,
                            outpo);
                    break;
                }
                case BMTREEIMG:
                case LIBMCSIMG: {
                    // Create clustering and traverse to create an image output
                    // Iterate inputs
                    final SmilesMemoizingMoleculeIterator sci =
                            MoleculeIo.molImporterMemoizingIterator(mi, params.errorHandling.getSuitableErrorHandler());

                    // Launch clustering
                    final FrameworkClusteringResults tree;

                    if (params.mode == BemisMurckoCliParameters.Mode.BMTREEIMG) {
                        tree = BemisMurckoClustering
                                .ofOrderedMoleculesIterator(sci)
                                .withErrorHandler(params.errorHandling.getSuitableErrorHandler())
                                .launch(po);
                    } else {
                        final LegacyLibraryMcsParameters lp = LegacyLibraryMcsAdapter.defaultParameters();
                        tree = LegacyLibraryMcsAdapter.cluster(lp, sci.transform( e -> e.getValue() ));
                    }


                    env.whenStat(t -> t.targetCount = sci.size());

                    // Traverse and write output
                    final SubProgressObserver outpo = env.progressObserver("Output", (stat, timer) -> stat.timestatExport = timer);

                    bmtreeimg_out(
                            tree,
                            sci.getMemoizedFunction().andThen(MoleculeIo.molImporterFromSourceFunction()),
                            out,
                            outpo);
                    break;
                }


                case DENDROGRAM: {
                    // Acquire clustering method
                    final BiFunction<DissimilarityInput, SubProgressObserver, IDBasedHierarchicClustering> clusteringMethod =
                            BemisMurckoCliParameters.clusteringMethods.parse(params.clus);

                    // Create dissimilarity input
                    final MolInputBuilder b = new MolInputBuilder();
                    b.addMolecules(
                            mi, // Use iterated molecules
                            StandardizerWrappers.chainOf( // With preprocessing
                                    StandardizerWrappers.aromatizeBasic(),
                                    StandardizerWrappers.removeAllExplicitH(),
                                    StandardizerWrappers.removeSmallFragments()
                            )
                    );

                    env.whenStat(t -> t.targetCount = b.size());

                    // Fingerpint and comparison method
                    final CfpGenerator gen = new CfpParameters().getDescriptorGenerator();
                    final CfpComparator comp = gen.forBinaryMetrics(BinaryMetrics.BINARY_TANIMOTO);

                    final MolInput input = b.build(gen, comp);

                    // Launch clustering algorithm
                    final IDBasedHierarchicClustering clustering = clusteringMethod.apply(input, po);

                    // Traverse and write output dendrogram
                    final SubProgressObserver outpo = env.progressObserver("Output", (stat, timer) -> stat.timestatExport = timer);

                    new DetailedClusteringRendering(clustering)
                            .levelAware(800, clustering.getPreferredAssigner())
                            .leafImageSize(of(250, 10))
                            .leafImage((leafid, renderer, area) -> renderer
                                    .setFontHeight(area.sy())
                                    .setColor("#000000")
                                    .placeHorizontalTextInto(input.getMolecule(leafid).getName(), Halign.LEFT, Valign.FILL, area, 0, 0)

                            )
                            .writeToPngImage(out, outpo);
                    break;
                }
                default :
                    throw new AssertionError(params.mode);
           }
        }
    }


    /**
     * Main method.
     *
     * @param args Command line argument. Use {@code -h} to get help
     * @throws IOException Propagated
     */
    public static void main(String [] args) throws IOException {
        CliInvocation
                .parseParameters(args, BemisMurckoCliParameters.class)
                .usage(BemisMurckoCliParameters::getHelp)
                .statProf(BemisMurckoCliStat.class)
                .launch(BemisMurckoCli::launch);
    }
}
