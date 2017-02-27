/*
 * Copyright 2017 ChemAxon Ltd.
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
import com.chemaxon.clustering.common.IDBasedSingleLevelClustering;
import com.chemaxon.clustering.common.MolInput;
import com.chemaxon.clustering.common.MolInputBuilder;
import com.chemaxon.clustering.sphex.SphereExclusion;
import com.chemaxon.descriptors.fingerprints.ecfp.EcfpComparator;
import com.chemaxon.descriptors.fingerprints.ecfp.EcfpGenerator;
import com.chemaxon.descriptors.fingerprints.ecfp.EcfpParameters;
import com.chemaxon.descriptors.metrics.BinaryMetrics;
import com.google.common.annotations.Beta;
import java.io.IOException;

/**
 * Example code for non-hierarchic clustering.
 *
 * <p>This example code
 * <ul>
 * <li>Reads molecules from standard input</li>
 * <li>Does trivial standardization: keeping largest fragments, dehydrogenization, aromatization</li>
 * <li>Invokes an adaptive sphere exclusion clustering to yield 5 to 10 clusters</li>
 * <li>Prints input structures along with their cluster IDs</li>
 * </ul>
 *
 *
 * <p>Please note that this class is marked with @Beta annotation, so it can be subject of incompatible changes or
 * removal in later releases.</p>
 *
 * @author Gabor Imre
 */
@Beta
public final class SphexExample {

    /**
     * This class uses only static methods, no instantiation will be done.
     */
    private SphexExample() {}

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

        final IDBasedSingleLevelClustering clus = SphereExclusion.adaptiveSPHEX(
                5,
                10,
                input,
                ProgressObservers.createForgivingNullObserver());
        System.err.println("  Clustering returned");

        System.err.println();


        // Print clustering info ---------------------------------------------------------------------------------------


        System.err.println("Cluster count: " + clus.clusters().size());
        System.err.println("Cluster sizes: ");
        for (int i = 0; i < clus.clusters().size(); i++) {
            System.err.println("  Cluster # " + i + ": " + clus.clusters().get(i).members().size());
        }

        // Print hierarchic indexes
        System.err.println("Cluster indexes (in order of input):");
        System.err.println();

        for (int i = 0; i < input.size(); i++) {
            System.err.print(input.getMolecule(i).getName());
            System.err.print(" ");
            System.err.print(clus.clusters().indexOf(clus.clusterOf(i).get()));
            System.err.println();
        }
    }

}
