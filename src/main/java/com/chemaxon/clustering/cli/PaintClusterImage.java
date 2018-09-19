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
package com.chemaxon.clustering.cli;

import com.chemaxon.clustering.common.IDBasedHierarchicCluster;
import com.chemaxon.overlap.cli.util.images.Area;
import com.chemaxon.overlap.cli.util.images.Renderer;

/**
 * Paint cluster illustration image to an area.
 *
 * @author Gabor Imre
 */
@FunctionalInterface
public interface PaintClusterImage {

    /**
     * Paint.
     *
     * @param cluster Cluster which representation should be painted
     * @param renderer Renderer to paint to
     * @param area Allocated area
     */
    void paint(IDBasedHierarchicCluster cluster, Renderer renderer, Area area);

}
