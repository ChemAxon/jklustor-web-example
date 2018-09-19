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

package com.chemaxon.clustering.examples;

import chemaxon.calculations.hydrogenize.Hydrogenize;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import com.chemaxon.descriptors.fingerprints.cfp.Cfp;
import com.chemaxon.descriptors.fingerprints.cfp.CfpComparator;
import com.chemaxon.descriptors.fingerprints.cfp.CfpGenerator;
import com.chemaxon.descriptors.fingerprints.cfp.CfpParameters;
import com.chemaxon.descriptors.metrics.BinaryMetrics;
import com.google.common.annotations.Beta;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example code for exercising the Descriptors API.
 *
 * <p>This example code
 * <ul>
 * <li>Reads molecules from standard input</li>
 * <li>Does trivial standardization: keeping largest fragments, dehydrogenization, aromatization</li>
 * <li>Invokes an exhaustive ChemicalFingerprint based dissimilarity matrix calculation</li>
 * <li>Prints input structures pair dissimilarities along with their IDs</li>
 * </ul>
 *
 * <p>Please note that the used APIs are intended for small scale programmatic comparisons. For calculating
 * dissimilarities of larger input sets more advanced tools are available.</p>
 *
 * <p>Please note that this class is marked with @Beta annotation, so it can be subject of incompatible changes or
 * removal in later releases.</p>
 *
 * @author Gabor Imre
 */
@Beta
public final class CfpDissimilarityExample {

    /**
     * This class uses only static methods, no instantiation will be done.
     */
    private CfpDissimilarityExample() {}

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

        // Collected standardized structures
        final List<Molecule> molecules = new ArrayList<>();

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
            molecules.add(largestFrag);

            // Print info
            System.err.println("Read mol: " + molecules.size() + " name: " + largestFrag.getName());
        }

        System.err.println();
        System.err.println("  All input read.");
        System.err.println();

        // Acquire descriptor/comparator to be used --------------------------------------------------------------------

        // Will use CFP
        final CfpGenerator gen = new CfpParameters.Builder().length(1024).bitsPerPattern(7).bondCount(3).build().getDescriptorGenerator();

        // With Tanimoto comparison metric
        final CfpComparator comp = gen.forBinaryMetrics(BinaryMetrics.BINARY_TANIMOTO);


        // Launch pairwise comparisons
        System.out.println("#1\t#2\tname 1\tname 2\tdissimilarity");
        for (int i = 0; i < molecules.size() - 1; i++) {
            final Molecule moli = molecules.get(i);
            final String namei = moli.getName();
            final Cfp fpi = gen.generateDescriptor(moli);

            for (int j = i + 1; j < molecules.size(); j++) {
                final Molecule molj = molecules.get(j);
                final String namej = molj.getName();
                final Cfp fpj = gen.generateDescriptor(molj);

                final double dissimilarity = comp.calculateDissimilarity(fpi, fpj);

                System.out.println(i + "\t" + j + "\t" + namei + "\t" + namej + "\t" + dissimilarity);
            }

        }
    }

}
