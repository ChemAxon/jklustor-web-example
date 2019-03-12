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
import com.chemaxon.clustering.common.IDBasedHierarchicCluster;
import com.chemaxon.clustering.common.IDBasedSingleLevelClustering;
import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO representing a non-hierarchic cluster.
 *
 * Contains only leaf nodes.
 *
 * @author Gabor Imre
 */
@XmlRootElement
@SuppressFBWarnings(
    value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
    justification = "Fields of this DTO is read by JSON serialization."
)
public class NonhierarchicCluster {

    /**
     * Unique integer ID of the represented cluster.
     *
     * Typically the index of the cluster in the associated {@link NonhierarchicClustering#clusters} /
     * {@link IDBasedSingleLevelClustering#clusters()}.
     */
    @XmlElement(required = true)
    public int clusterId;

    /**
     * A member of this cluster.
     *
     * @see Cluster#representant()
     */
    @XmlElement(required = true)
    public int representantId;

    /**
     * Contained members.
     */
    @XmlElement(required = true)
    public List<Integer> memberIds;

    /**
     * Member count.
     */
    @XmlElement(required = true)
    public int size;


    /**
     * Construct from an {@link IDBasedHierarchicCluster}.
     *
     * @param clusterId  Unique cluster ID
     * @param cluster Cluster
     */
    public NonhierarchicCluster(int clusterId, Cluster<Integer> cluster) {
        this.clusterId = clusterId;
        this.representantId = cluster.representant();
        this.size = cluster.memberCount();
        this.memberIds = ImmutableList.copyOf(cluster.members());
    }

}
