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

import com.chemaxon.overlap.cli.util.images.Area;
import com.chemaxon.overlap.cli.util.images.Px2d;
import com.chemaxon.overlap.cli.util.images.Renderer;
import java.util.List;

/**
 * Paint a decoration of a query group before painting queries.
 *
 * @author Gabor Imre
 */
@FunctionalInterface
public interface PaintLeafGroupDecoration {

    /**
     * Paint.
     *
     * @param renderer Renderer to paint to
     * @param area Area of the leaves to be painted later
     * @param branchEnds List of branch endpoints to be filled
     */
    void paint(Renderer renderer, Area area, List<Px2d> branchEnds);

}
