/*
 * Copyright 2016 ChemAxon Ltd.
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

package com.chemaxon.clustering.examples;

import chemaxon.calculations.hydrogenize.Hydrogenize;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import com.chemaxon.calculations.common.ProgressObservers;
import com.chemaxon.clustering.common.Cluster;
import com.chemaxon.clustering.common.IDBasedHierarchicClusterBuidler;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.chemaxon.clustering.common.MolInput;
import com.chemaxon.clustering.common.MolInputBuilder;
import com.chemaxon.clustering.common.SingleLevelClustering;
import com.chemaxon.clustering.util.Util;
import com.chemaxon.clustering.wards.LanceWilliamsAlgorithm;
import com.chemaxon.clustering.wards.LanceWilliamsMerges;
import com.chemaxon.descriptors.fingerprints.ecfp.EcfpComparator;
import com.chemaxon.descriptors.fingerprints.ecfp.EcfpGenerator;
import com.chemaxon.descriptors.fingerprints.ecfp.EcfpParameters;
import com.chemaxon.descriptors.metrics.BinaryMetrics;
import com.google.common.annotations.Beta;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example code for hierarchic clustering.
 *
 * <p>This example code
 * <ul>
 * <li>Reads molecules from standard input</li>
 * <li>Does trivial standardization: keeping largest fragments, dehydrogenization, aromatization</li>
 * <li>Builds a hierarchic clustering over them using Wards algorithm with ECFP fingerprints/Tanimoto metric</li>
 * <li>Prints input structures along with their hierarchic indexes
 * (see {@link Util#hierarchicIndexOf(java.lang.Object, com.chemaxon.clustering.common.HierarchicClustering) })</li>
 * <li>Does a partitioning over the hierarchy, prints molecule names and partiton IDs to the standard out</li>
 * <li>Prints hierarchy (with molecule names) to std err</li>
 * </ul>
 *
 * <p>Please note that this class is marked with @Beta annotation, so it can be subject of incompatible changes or
 * removal in later releases.</p>
 *
 * @author Gabor Imre
 */
@Beta
public final class HierarchicClusteringExample {

    /**
     * This class uses only static methods, no instantiation will be done.
     */
    private HierarchicClusteringExample() {}

    /**
     * Main method.
     *
     * <p>For the sake of readability all possible checked exceptions will be thrown.</p>
     *
     * @param args  Command line arguments. No arguments accepted.
     *
     * @throws  IOException     Re-thrown exception
     */
    public static void main(String [] args) throws IOException {

        // Ensure that no command line arguments passed
        if (args.length != 0) {
            throw new IllegalArgumentException("No command line argument(s) accepted, passed " + args.length);
        }

        // Read molecules from stdin, standardize them and store them into a dissimilarity input builder ---------------
        System.err.println("Read molecules from stdin");
        System.err.println();

        // Importer to handle structure input
        final MolImporter molImporter = new MolImporter(System.in);

        // Builder to collect standardized structures
        final MolInputBuilder inputBuilder = new MolInputBuilder();

        // read input
        Molecule readMol;
        while ((readMol = molImporter.read()) != null) {

            // Aromatize read structure
            readMol.aromatize(Molecule.AROM_BASIC);

            // Dehydrogenize read structure
            Hydrogenize.convertExplicitHToImplicit(readMol);

            // Keep the largest fragment
            final Molecule [] frags = readMol.convertToFrags();
            Molecule largestFrag = frags[0];
            for (int i = 1; i < frags.length; i++) {
                if (frags[i].getAtomCount() > largestFrag.getAtomCount()) {
                    largestFrag = frags[i];
                }
            }

            largestFrag.setName(readMol.getName());

            // Put it into the builder
            inputBuilder.addMolecule(largestFrag);

            // Print info
            System.err.println("Read mol: " + inputBuilder.size() + " name: " + largestFrag.getName());
        }

        System.err.println();
        System.err.println("  All input read.");
        System.err.println();

        // Acquire descriptor/comparator to be used --------------------------------------------------------------------
        // Will use default ECFP
        final EcfpGenerator gen = (new EcfpParameters()).getDescriptorGenerator();
        final EcfpComparator comp = gen.getBinaryMetricsComparator(BinaryMetrics.BINARY_TANIMOTO);

        // Construct dissimilarity input
        final MolInput input = inputBuilder.build(gen, comp);

        // Launch clustering -------------------------------------------------------------------------------------------
        System.err.println("Launch clustering");

        final IDBasedHierarchicClustering clus = LanceWilliamsAlgorithm.cluster(
                input,
                new LanceWilliamsMerges.Wards(),
                ProgressObservers.createForgivingNullObserver());
        System.err.println("  Clustering returned");

        System.err.println();


        // Print clustering info ---------------------------------------------------------------------------------------
        System.err.println("Max depth: " + Util.maxDepth(clus));
        System.err.println("Max level: " + clus.getPreferredAssigner().maxLevel());

        // Parint various String representations
        System.err.println();
        System.err.println("String representation of the hierarchy");
        System.err.println(IDBasedHierarchicClusterBuidler.toDetailedString(clus));

        System.err.println();
        System.err.println("JSon-like representation of the hierarchy");
        System.err.println(Util.toJsonMixed(clus));

        // Print hierarchic indexes
        System.err.println("Hierarchic indexes (in order of input):");
        System.err.println();

        for (int i = 0; i < input.size(); i++) {
            System.err.print(input.getMolecule(i).getName());
            System.err.print(" ");
            System.err.print(Util.hierarchicIndexOf(i, clus));
            System.err.println();
        }

        // Print Distinct cluster levels vs partition counts -----------------------------------------------------------
        System.err.println();
        System.err.println("Leveles vs partition counts:");
        System.err.println();
        final double [] levels = clus.getPreferredAssigner().getDistinctClusterLevels();
        final int [] counts = clus.getPreferredAssigner().getClusterCountsForLevels();

        for (int i = 0; i < levels.length; i++) {
            System.err.print(levels[i]);
            System.err.print("\t");
            System.err.println(counts[i]);

        }

        System.err.println();

        // Also print cluster levels bs partition counts as a scatter plot ---------------------------------------------
        final Util.StringScatterPlot plot = new Util.StringScatterPlot();
        for (int i = 0; i < levels.length; i++) {
            plot.addDataPoint(levels[i], counts[i]);
        }
        System.err.println(plot.toString(120, 35, true, true));

        System.err.println();

        // Visualize clustering hierarchy ------------------------------------------------------------------------------
        // Fill names to leaf IDs
        final List<String> names = new ArrayList<String>();
        for (int i = 0; i < input.size(); i++) {
            names.add(input.getMolecule(i).getName());
        }
        System.err.println(Util.hierarchyToString(clus, clus.getPreferredAssigner(), names, 120));

        // Select a level to partition the clustering ------------------------------------------------------------------
        // Examine the previously acquired distinct partition level / count values, find a partitioning level which
        // results in at least 10 clusters, then invoke partitioning


        double levelToPartition = levels[0];
        int partitionCount = counts[0];
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] >= 10 && counts[i] < partitionCount) {
                levelToPartition = levels[i];
                partitionCount =   counts[i];
            }
        }

        System.err.println();
        System.err.println("Level to partition: "     + levelToPartition);
        System.err.println("Expected cluster count: " + partitionCount);


        SingleLevelClustering<Integer, ? extends Cluster<Integer>> partition =
                clus.getPreferredAssigner().partition(levelToPartition);

        System.err.println();
        System.err.println("Partition count: " + partition.clusters().size());

        // Print partitions
        System.err.println("Partition sizes: ");
        for (int i = 0; i < partition.clusters().size(); i++) {
            System.err.println("  Partition # " + i + ": " + partition.clusters().get(i).members().size());
        }
        System.err.println();
        System.err.println("Partition indexes (in order of input):");
        System.err.println();

        for (int i = 0; i < input.size(); i++) {
            System.err.print(input.getMolecule(i).getName());
            System.err.print(" ");
            System.err.print(partition.clusters().indexOf(partition.clusterOf(i).get()));
            System.err.println();
        }

    }

}
