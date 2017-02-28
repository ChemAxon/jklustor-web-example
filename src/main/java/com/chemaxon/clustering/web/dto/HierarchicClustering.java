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
package com.chemaxon.clustering.web.dto;

import com.chemaxon.clustering.common.IDBasedAssigner;
import com.chemaxon.clustering.common.IDBasedHierarchicCluster;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO representing a clustering.
 *
 * This representation aggregates the following:
 * <ul>
 * <li>Clustering hierarchy from {@link IDBasedHierarchicClustering}</li>
 * <li>A clustering level (position) assignment from a {@link IDBasedAssigner}</li>
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
public class HierarchicClustering {

    /**
     * Highest level cluster(s).
     *
     * At least one element must be present.
     */
    @XmlElement(required = true)
    public List<HierarchicCluster> roots;

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
     * Cluster/leaf position (level) human readable description.
     */
    @XmlElement(required = true)
    public String positionDescription;

    /**
     * Fill {@link #roots} and {@link #positionDescription}.
     *
     * @param clustering Clustering to represent
     * @param assigner Level assigner
     */
    public void setClustering(IDBasedHierarchicClustering clustering, IDBasedAssigner assigner) {
        final ImmutableList.Builder<HierarchicCluster> rb = new ImmutableList.Builder<>();
        for (IDBasedHierarchicCluster c : clustering.roots()) {
            rb.add(new HierarchicCluster(c, assigner));
        }
        this.roots = rb.build();
        this.positionDescription = assigner.toString();
    }



}
