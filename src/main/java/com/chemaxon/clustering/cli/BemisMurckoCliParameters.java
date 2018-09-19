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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.chemaxon.apidiscovery.linenote.ChoiceDescriptor;
import com.chemaxon.apidiscovery.linenote.Linenote;
import com.chemaxon.apidiscovery.linenote.LinenoteBuilder;
import com.chemaxon.apidiscovery.linenote.Linenotes;
import com.chemaxon.calculations.common.SubProgressObserver;
import com.chemaxon.calculations.io.SimpleErrorHandling;
import com.chemaxon.clustering.common.DissimilarityInput;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.chemaxon.clustering.wards.LanceWilliamsAlgorithm;
import com.chemaxon.clustering.wards.LanceWilliamsMerges;
import java.util.function.BiFunction;

/**
 * CLI options for {@link ClusterCli}.
 *
 * @author Gabor Imre
 */
public class BemisMurckoCliParameters {

    public static final Linenote<BiFunction<DissimilarityInput, SubProgressObserver, IDBasedHierarchicClustering>> clusteringMethods;

    static {
        LinenoteBuilder<BiFunction<DissimilarityInput, SubProgressObserver, IDBasedHierarchicClustering>> b = Linenotes.builder();
        b.addSimpleChoice(
                "complete-linkage",
                "Complete linkage clustering using Lance-Williams algorithm.",
                () -> (DissimilarityInput input, SubProgressObserver po)
                        -> LanceWilliamsAlgorithm.cluster(input, new LanceWilliamsMerges.CompleteLinkage(), po)
        );
        b.addSimpleChoice(
                "single-linkage",
                "Single linkage clustering using Lance-Williams algorithm.",
                () -> (DissimilarityInput input, SubProgressObserver po)
                        -> LanceWilliamsAlgorithm.cluster(input, new LanceWilliamsMerges.SingleLinkage(), po)
        );
        b.addSimpleChoice(
                "average-linkage",
                "Average linkage clustering using Lance-Williams algorithm.",
                () -> (DissimilarityInput input, SubProgressObserver po)
                        -> LanceWilliamsAlgorithm.cluster(input, new LanceWilliamsMerges.AverageLinkage(), po)
        );
        b.addSimpleChoice(
                "wards",
                "Wards clustering using Lance-Williams algorithm.",
                () -> (DissimilarityInput input, SubProgressObserver po)
                        -> LanceWilliamsAlgorithm.cluster(input, new LanceWilliamsMerges.Wards(), po)
        );


        clusteringMethods = b.build();
    }


    // Parameters managed by CliInvocation utilities -------------------------------------------------------------------
    @Parameter(names = "-h", help = true, description = "Print help and exit")
    public boolean help = false;

    @Parameter(names = "-stat", description = "File to write statistics (in JSON format) on execution steps.")
    public String stat = null;

    @Parameter(names = "-statgcinitial", description = "Invoke Java garbage collection before storing the initial"
            + " memory info snapshot in execution statistics.")
    public boolean statGcInitial = false;

    @Parameter(names = "-statgcfinial", description = "Invoke Java garbage collection before storing the final"
            + " memory info snapshot in execution statistics.")
    public boolean statGcFinal = false;

    @Parameter(names = "-prof", description = "File to write detailed VM profiling data.")
    public String prof = null;

    @Parameter(names = "-profres", description = "Resolution of the profiling info written when option \"-prof <FILE>\""
            + " specified. Update time between writes in ms.")
    public int profres = 1000;


    @Parameter(names = "-in", description = "Structures to read")
    public String in = "-";

    @Parameter(names = "-out", description = "Output to write. Note that binary outputs (for example PNG image file"
            + " from the execution of mode \"BMTREEIMG\") is also writen here.")
    public String out = "-";

    @Parameter(names = "-mode", description = "Run mode. Use \"READMOLS\" to just read input molecules, \"READWRITE\""
            + " to read and write back molecules as SMILES, \"READWRITECANONIC\" to read and write back molecules as"
            + " canonical SMILES, \"BMF\" to calculate and write bemis-murcko frameworks as SMILES, \"BMTREEMOL\" to"
            + " write bemis-murcko clastering in a traversed molecule format or \"BMTREEIMG\" to generate an image"
            + " from the clustering. Use \"LIBMCSIMG\" to launch Legacy Library MCS clustering and write dendrogram"
            + " image. Use \"DENDROGRAM\" to launch a clustering and write a dendrogram image.")
    public Mode mode = Mode.READMOLS;

    @Parameter(names = "-errorhandling", description = "Input related error handling. Use \"FAIL\", \"LOG\" or"
            + " \"SILENT\". Please note that the value of this option is just a suggestion for implementation"
            + " specific behavior.")
    public SimpleErrorHandling errorHandling = SimpleErrorHandling.FAIL;

    @Parameter(names = "-clus", description = "Specify clustering method for when  \"-mode DENDROGRAM\" used. For other"
            + " values of \"-mode <MODE>\" the value of this parameter is not checked.")
    public String clus = "complete-linkage";



    /**
     * Execution mode.
     */
    public enum Mode {
        /**
         * Just read input structures.
         */
        READMOLS,

        /**
         * Read inputs, write them back as SMILES.
         */
        READWRITE,

        /**
         * Read inputs, write them back as canonical SMILES.
         */
        READWRITECANONIC,

        /**
         * Calculate BM frameworks, write input structures and frameworks as SMILES.
         */
        BMF,

        /**
         * Write Bemis-Murcko clustering tree as SMILES.
         */
        BMTREEMOL,

        /**
         * Write Bemisu-Murcko clustering as an image.
         */
        BMTREEIMG,

        /**
         * Write legacy LibMcs clustering as an image.
         */
        LIBMCSIMG,

        /**
         * Launch a clustering and write a dendrogram image.
         */
        DENDROGRAM
    }

    /**
     * Process CLI arguments.
     *
     * @param args Arguments passed to CLI {@code main(String [] args)} method
     * @return Parsed arguments
     */
    public static BemisMurckoCliParameters parseCliArguments(String [] args) {
        final BemisMurckoCliParameters ret = new BemisMurckoCliParameters();
        new JCommander(ret, args);
        return ret;
    }

    /**
     * Construct help message.
     *
     * @return Help message
     */
    public static String getHelp() {
        final StringBuilder ret = new StringBuilder();

        // Collect usage from JCommander
        final JCommander jc = new JCommander(new BemisMurckoCliParameters());
        jc.usage(ret);
        ret.append('\n');

        // Add help on clustering methods
        // Note that arguments of the possible commands are not printed
        ret.append("  Applicable clustering methods for option \"-clus <METHOD>\"\n");
        ret.append('\n');

        for (ChoiceDescriptor choice : clusteringMethods.choiceDescriptors()) {
            ret.append("    ");
            ret.append(choice.getLabel());
            ret.append('\t');
            ret.append(choice.getDescription());
            ret.append('\n');
        }

        return ret.toString();
    }
}
