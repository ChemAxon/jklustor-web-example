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

import com.chemaxon.clustering.common.IDBasedAssigner;
import com.chemaxon.clustering.common.IDBasedHierarchicCluster;
import com.google.common.collect.ImmutableList;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO representing a hierarchic cluster.
 *
 * A hierarchic might contain individual structures (leaves) and further clusters.
 *
 * @author Gabor Imre
 */
@XmlRootElement
@SuppressFBWarnings(
    value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
    justification = "Fields of this DTO is read by JSON serialization."
)
public class HierarchicCluster {

    /**
     * Unique integer ID of the represented cluster.
     */
    @XmlElement(required = true)
    public int clusterId;


    /**
     * Position of the cluster.
     */
    @XmlElement(required = true)
    public double clusterPosition;


    /**
     * Contained clusters.
     */
    @XmlElement(required = true)
    public List<HierarchicCluster> clusters;

    /**
     * Contained leaves.
     */
    @XmlElement(required = true)
    public List<Integer> leafIds;

    /**
     * Positions for the leaves.
     */
    @XmlElement(required = true)
    public List<Double> leafPositions;


    /**
     * Construct from an {@link IDBasedHierarchicCluster}.
     *
     * @param base Cluster to represent
     * @param assigner Level assigner to use
     */
    public HierarchicCluster(IDBasedHierarchicCluster base, IDBasedAssigner assigner) {
        this.clusterPosition = assigner.clusterLevel(base);
        this.clusterId = base.getClusterID();

        // Fill clusters
        final ImmutableList.Builder<HierarchicCluster> clb = new ImmutableList.Builder<HierarchicCluster>();
        for (IDBasedHierarchicCluster c : base.clusters()) {
            clb.add(new HierarchicCluster(c, assigner));
        }
        this.clusters = clb.build();

        // Fill leaves
        final ImmutableList.Builder<Integer> lidb = new ImmutableList.Builder<Integer>();
        final ImmutableList.Builder<Double> lpb = new ImmutableList.Builder<Double>();
        for (Integer lid : base.leaves()) {
            lidb.add(lid);
            lpb.add(assigner.leafLevel(lid));
        }
        this.leafIds = lidb.build();
        this.leafPositions = lpb.build();
    }

}
