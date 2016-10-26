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
package com.chemaxon.clustering.web.entities;

import com.chemaxon.clustering.common.IDBasedAssigner;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;

/**
 *
 *
 * @author Gabor Imre
 */
public class Clustering {

    /**
     * Represented clustering.
     */
    private final IDBasedHierarchicClustering clustering;

    /**
     * Represented level assigner.
     */
    private final IDBasedAssigner assigner;

    /**
     * Elapsed time in milliseconds.
     *
     * Used for benchmark purposes.
     */
    private final long elapsedTime;


    /**
     * Algorithm description.
     */
    private final String algorithmDescription;

    /**
     * Create.
     *
     * @param clustering Clustering to represent
     * @param elapsedTime Elapsed time to store
     * @param algorithmDescription Description of the c
     */
    public Clustering(IDBasedHierarchicClustering clustering, long elapsedTime, String algorithmDescription) {
        this.clustering = clustering;
        this.elapsedTime = elapsedTime;
        this.assigner = clustering.getPreferredAssigner();
        this.algorithmDescription = algorithmDescription;
    }


    public IDBasedAssigner getAssigner() {
        return this.assigner;
    }

    public IDBasedHierarchicClustering getClustering() {
        return this.clustering;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public String getAlgorithmDescription() {
        return algorithmDescription;
    }




}
