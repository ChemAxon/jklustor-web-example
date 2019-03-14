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

package com.chemaxon.clustering.web.services;

import com.chemaxon.calculations.common.ProgressObservers;
import com.chemaxon.clustering.common.DissimilarityInput;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.chemaxon.clustering.common.MolInputBuilder;
import com.chemaxon.clustering.wards.LanceWilliamsAlgorithm;
import com.chemaxon.clustering.wards.LanceWilliamsMerge;
import com.chemaxon.clustering.web.dao.ClusteringDao;
import com.chemaxon.clustering.web.entities.Clustering;
import com.chemaxon.clustering.web.entities.Molfile;
import com.chemaxon.descriptors.fingerprints.cfp.CfpComparator;
import com.chemaxon.descriptors.fingerprints.cfp.CfpGenerator;
import com.chemaxon.descriptors.fingerprints.cfp.CfpParameters;
import com.chemaxon.descriptors.metrics.BinaryMetrics;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides domain specific operations for Clustering instances.
 *
 * @author Gabor Imre
 */
@Service
public class ClusteringService {

    @Autowired
    private ClusteringDao clusteringDao;

    @Autowired
    private MolfilesService molfilesService;

    /**
     * Invoke a clustering.
     *
     * @param molfile Structures to be clustered
     * @param algorithm Clustering algorithm to be used
     * @param idSuggestion Suggestion for ID of the result
     * @return Executed clustering
     */
    public Clustering invokeLanceWilliams(Molfile molfile, LanceWilliamsMerge algorithm, String idSuggestion) {


        final long timeStart = System.currentTimeMillis();

        // Acquire descriptor generator and comparator (fingerprint and metric) to be used
        // Use cfp7-1
        final CfpGenerator gen = CfpParameters.createNewBuilder()
                .length(1024)
                .bitsPerPattern(1)
                .bondCount(7)
                .build().getDescriptorGenerator();
        // With tanimoto
        final CfpComparator cmp = gen.forBinaryMetrics(BinaryMetrics.BINARY_TANIMOTO);

        // Create dissimilarity input
        final DissimilarityInput dissim = new MolInputBuilder(molfile.getAllMolecules()).build(gen, cmp);

        // Launch clustering with no progress observing
        final IDBasedHierarchicClustering res = LanceWilliamsAlgorithm.cluster(dissim, algorithm, ProgressObservers.nullProgressObserver());

        final long timeStop = System.currentTimeMillis();

        final Clustering clustering = new Clustering(res, timeStop - timeStart, algorithm.toString());

        this.clusteringDao.add(idSuggestion, clustering);

        return clustering;
    }


    /**
     * Retrieve Clustering by ID.
     *
     * @param id ID of {@link Clustering} to retrieve
     * @return Instance identified by the given ID.
     * @throws NoSuchElementException when given ID not found.
     */
    public Clustering getClustering(String id) {
        return this.clusteringDao.get(id);
    }


    /**
     * Retrieve all Clustering instances.
     *
     * @return ID to Clustering mapping.
     */
    public Map<String, Clustering>  getAllClusterings() {
        return this.clusteringDao.getAll();
    }



    /**
     * Retrieve Clustering ID.
     *
     * @param clustering Instance
     * @return ID of instance
     * @throws NoSuchElementException when given ID not found for the specified instance
     */
    public String getClusteringId(Clustering clustering) {
        return this.clusteringDao.getIdOf(clustering);
    }

    /**
     * Delete an existing clustering.
     *
     * @param clustering Instance
     */
    public void deleteClustering(Clustering clustering) {
        this.clusteringDao.delete(clustering);
    }

    /**
     * Delete all clusterings.
     */
    public void deleteAllClusterings() {
        this.clusteringDao.deleteAll();
    }


}
