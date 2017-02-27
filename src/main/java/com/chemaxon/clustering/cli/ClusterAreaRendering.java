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
package com.chemaxon.clustering.cli;

import com.chemaxon.clustering.common.IDBasedHierarchicCluster;
import com.chemaxon.overlap.cli.util.images.Area;
import com.chemaxon.overlap.cli.util.images.Px2d;
import com.chemaxon.overlap.cli.util.images.Renderer;
import com.chemaxon.overlap.cli.util.images.SimplePane;
import com.google.common.base.Optional;
import java.util.List;

/**
 * Rendering of cluster images and branches.
 *
 * @author Gabor Imre
 */
public interface ClusterAreaRendering {

    /**
     * Required horizontal pixel count of the clusters drawing area.
     *
     * Vertical size of clusters/leaves area is determined by the leaves and their grouping.
     *
     * @return Horizontal size of clusters drawing area
     */
    int horizontalPixelCount();

    /**
     * Optional scale area placed above tree.
     *
     * @return Pane to be placed above clusters
     */
    Optional<SimplePane> createScaleArea();

    /**
     * Paint cluster and is branches.
     *
     * @param cluster Cluster to paint.
     * @param renderer Target renderer to render to.
     * @param clustersArea {@link Area} of the clusters part on the target {@link Renderer}
     * @param childrenEndpoints Endpoints of children to connect. All endpoint is expected to be connected. List of
     * endpoints should not be modified by the implementation.
     * @return Endpoint of this cluster to connect parent by clusters/roots
     */
    Px2d paintCluster(IDBasedHierarchicCluster cluster, Renderer renderer, Area clustersArea, List<Px2d> childrenEndpoints);

}
