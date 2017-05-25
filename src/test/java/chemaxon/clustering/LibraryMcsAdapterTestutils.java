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
package chemaxon.clustering;

import com.chemaxon.clustering.common.IDBasedHierarchicCluster;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.chemaxon.clustering.framework.FrameworkClusteringResults;
import java.util.List;
import org.apache.commons.logging.Log;

/**
 * Test utils for exercising {@link FrameworkClusteringResults} traversal.
 *
 * @author Gabor Imre
 */
public final class LibraryMcsAdapterTestutils {

    /**
     * No constructor exposed.
     */
    private LibraryMcsAdapterTestutils() {};

    /**
     * Recursive descent step.
     *
     * @param log Logger to write
     * @param results Results object
     * @param cluster Cluster to print out and traverse
     * @param prefix Prefix for each printed line
     * @param mols Structures referenced by {@link IDBasedHierarchicCluster#leaves()}
     */
    private static void traverseIdBasedCluster(
            Log log,
            FrameworkClusteringResults results,
            IDBasedHierarchicCluster cluster,
            String prefix,
            List<String> mols) {

        log.debug(prefix + "- Framework: " + results.getFrameworkAsSmiles(cluster.getClusterID()));
        for (Integer m : cluster.leaves()) {
            log.debug(prefix + "  Molecule (" + m + "): " + mols.get(m));
        }

        for (IDBasedHierarchicCluster child : cluster.clusters()) {
            traverseIdBasedCluster(
                    log,
                    results,
                    child,
                    prefix + "    ",
                    mols);
        }
    }

    /**
     * Traverse {@link IDBasedHierarchicClustering} of a {@link FrameworkClusteringResults}.
     *
     * Traverse hierarchy and print it with leaf structures to {@link Log#debug(java.lang.Object)}.
     *
     * @param log Logger to write to using {@link Log#debug(java.lang.Object)}
     * @param results Results object of clustering
     * @param mols Structures referenced by {@link IDBasedHierarchicCluster#leaves()}
     */
    public static void printoutHierarchy(Log log, FrameworkClusteringResults results, List<String> mols) {
        if (!log.isDebugEnabled()) {
            return;
        }
        log.debug("Hierarchy:");
        log.debug("  Roots count:      " + results.getHierarchy().roots().size());
        log.debug("  Max cluster ID:   " + results.getHierarchy().getMaxClusterID());
        log.debug("  Max leaf ID:      " + results.getHierarchy().getMaxLeafID());
        log.debug("  Max height:       " + results.getHierarchy().maxHeight());
        log.debug("  Frameworks count: " + results.getFrameworksCount());


        for (IDBasedHierarchicCluster root : results.getHierarchy().roots()) {
            traverseIdBasedCluster(
                    log,
                    results,
                    root,
                    "  ",
                    mols
            );
        }
    }

}
