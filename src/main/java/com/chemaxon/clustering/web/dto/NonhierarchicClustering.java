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
package com.chemaxon.clustering.web.dto;

import com.chemaxon.clustering.common.Cluster;
import com.chemaxon.clustering.common.IDBasedSingleLevelClustering;
import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO representing a single level clustering.
 *
 * This representation aggregates the following:
 * <ul>
 * <li>Clustering info from {@link IDBasedSingleLevelClustering}</li>
 * <li>References to represented structures</li>
 * <li>Benchmarking information</li>
 * </ul>
 *
 * @author Gabor Imre
 */
@XmlRootElement
@SuppressFBWarnings(
    value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
    justification = "Fields of this DTO is read by JSON serialization."
)
public class NonhierarchicClustering {

    /**
     * Cluster(s).
     *
     * At least one element must be present in each clusters.
     */
    @XmlElement(required = true)
    public List<NonhierarchicCluster> clusters;

    /**
     * ID of this clustering.
     */
    @XmlElement(required = true)
    public String id;

    /**
     * URL of this clustering.
     */
    @XmlElement(required = true)
    public String url;

    /**
     * Elapsed time of the clustering in ms.
     */
    @XmlElement(required = true)
    public long elapsedTime;

    /**
     * Represented algorithm human readable description.
     */
    @XmlElement(required = true)
    public String algorithmDescription;

    /**
     * Fill {@link #roots} and {@link #positionDescription}.
     *
     * @param clustering Clustering to represent
     */
    public void setClustering(IDBasedSingleLevelClustering clustering) {
        final ImmutableList.Builder<NonhierarchicCluster> rb = new ImmutableList.Builder<>();
        final List<Cluster<Integer>> inputClusters = clustering.clusters();
        for (int i = 0; i < inputClusters.size(); i++) {
            final Cluster<Integer> inputCluster = inputClusters.get(i);
            rb.add(new NonhierarchicCluster(i, inputCluster));
        }
        this.clusters = rb.build();
    }



}
