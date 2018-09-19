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

import com.chemaxon.overlap.cli.util.images.Px2d;
import com.chemaxon.overlap.cli.util.images.Renderer;
import java.util.List;

/**
 * Paint tree branches between a cluster and its descendants.
 *
 * @author Gabor Imre
 */
@FunctionalInterface
public interface PaintClusterBranches {

    /**
     * Paint.
     *
     * @param startpoint Start point of branches. For level aware dendrograms the vertical bar depicting the cluster is
     * expected to be rendered at this location
     * @param endpoints End points of branches
     * @param renderer Renderer
     */
    void paint(Px2d startpoint, List<Px2d> endpoints, Renderer renderer);

}
