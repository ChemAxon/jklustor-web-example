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

import com.chemaxon.calculations.common.SubProgressObserver;
import com.chemaxon.calculations.sc.util.MoreLists;
import static com.chemaxon.calculations.sc.util.MoreLists.firstElementOf;
import static com.chemaxon.calculations.sc.util.MoreLists.lastElementOf;
import static com.chemaxon.calculations.sc.util.MoreLists.removeLastElementOf;
import com.chemaxon.clustering.common.IDBasedAssigner;
import com.chemaxon.clustering.common.IDBasedHierarchicCluster;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.chemaxon.clustering.util.Util;
import com.chemaxon.overlap.cli.util.images.Area;
import com.chemaxon.overlap.cli.util.images.AreaSpec;
import com.chemaxon.overlap.cli.util.images.BufferedImageRenderer;
import com.chemaxon.overlap.cli.util.images.BufferedImageRendering;
import com.chemaxon.overlap.cli.util.images.GridPane;
import com.chemaxon.overlap.cli.util.images.Halign;
import com.chemaxon.overlap.cli.util.images.Px2d;
import static com.chemaxon.overlap.cli.util.images.Px2d.of;
import com.chemaxon.overlap.cli.util.images.Renderer;
import com.chemaxon.overlap.cli.util.images.Rendering;
import com.chemaxon.overlap.cli.util.images.SimplePane;
import com.chemaxon.overlap.cli.util.images.SubPane;
import com.chemaxon.overlap.cli.util.images.Valign;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Paint clustering hierarchy.
 *
 * @author Gabor Imre
 */
public class DetailedClusteringRendering {


    /**
     * Hierarchy to paint.
     */
    private final IDBasedHierarchicClustering hierarchy;

    /**
     * Size of cluster preview image.
     */
    private Px2d clusterImageSize;

    /**
     * Size of leaf preview image.
     */
    private Px2d leafImageSize;

    /**
     * Number of columns for leaves.
     */
    private int leafMaxCols;

    /**
     * Number of pixels between leaves of the same cluster.
     */
    private int leafSeparation;

    /**
     * Number of pixels between leaves of different clusters.
     */
    private int leafGroupSeparation;

    /**
     * Horizontal separation between clustering levels.
     */
    private int clusterSeparation;

    /**
     * Separation between the clusters and the leaves area.
     */
    private int clustersLeavesSeparation;

    /**
     * Paint cluster image.
     */
    private PaintClusterImage paintClusterImage;


    /**
     * Paint leaf image.
     */
    private PaintLeafImage paintLeafImage;

    /**
     * Paint cluster branches.
     */
    private PaintClusterBranches paintClusterBranches;

    /**
     * Decoration for multi column leaf groups.
     */
    private PaintLeafGroupDecoration paintLeafGroupDecoration;

    /**
     * Rendering of clusters area.
     */
    private ClusterAreaRendering clusterAreaRendering;

    /**
     * Border in pixels.
     */
    private int borderLeft;

    /**
     * Border in pixels.
     */
    private int borderRight;

    /**
     * Border in pixels.
     */
    private int borderTop;

    /**
     * Border in pixels.
     */
    private int borderBottom;

    /**
     * Construct.
     *
     * @param hierarchy Clustering hierarchy
     */
    public DetailedClusteringRendering(IDBasedHierarchicClustering hierarchy) {
        this.hierarchy = hierarchy;
        this.clusterImageSize = Px2d.of(7, 7);
        this.leafImageSize = Px2d.of(5, 5);
        this.leafMaxCols = 1;
        this.leafSeparation = 1;
        this.leafGroupSeparation = 1;
        this.clustersLeavesSeparation = 5;
        this.clusterSeparation = 30;
        this.paintClusterImage = (cluster, renderer, area) -> renderer.setColor("#000000").fillEllipse(area);
        this.paintLeafImage = (leaf, renderer, area) -> renderer.setColor("#000000").drawEllipse(area);
        this.paintClusterBranches = (startpoint, endpoints, renderer) -> {
            if (endpoints.size() == 1) {
                // only one horizontal line
                renderer.drawLine(startpoint, firstElementOf(endpoints));
            } else {
                renderer.drawLine(startpoint.sety(firstElementOf(endpoints).y), startpoint.sety(lastElementOf(endpoints).y));
                // renderer.drawLine(startpoint, startpoint.setx(x));
                for (Px2d p : endpoints) {
                    renderer.drawLine(p.setx(startpoint.x), p);
                }
            }
        };
        this.paintLeafGroupDecoration = (renderer, area, branchEnds) -> {
            renderer
                    .setColor("#DDDDDD")
                    .drawBorder(area, 1);
            branchEnds.add(area.pLeftCenter().plusx(-2));

        };
        this.borderLeft = 5;
        this.borderBottom = 5;
        this.borderRight = 5;
        this.borderTop = 5;
        this.clusterAreaRendering = new DefaultClusterAreaRendering();
    }


    /**
     * Render to a target rendering.
     * @param rendering Renderer
     * @param po Progress observer to track progress
     * @param <T> Renderer type created
     * @return Renderer with tree painted
     */
    public <T extends Renderer> T renderTo(Rendering<T> rendering, SubProgressObserver po) {
        final Layout layout = new Layout(this.clusterAreaRendering);
        final T renderer = rendering.layout(layout);

        if (layout.scaleArea.isPresent()) {
            // Paint scale
            layout.scaleArea.get().paint(renderer);
        }

        // Endpoints for branches
        List<List<Px2d>> allBranchEnds = new ArrayList<>();

        // Track leaves y position
        final MutableInt height = new MutableInt();
        Util.traverseSimplePostOrderDfs(this.hierarchy, (path, visited) -> {

            /*
            System.err.println();
            System.err.println("Traversal step");
            System.err.println();
            System.err.println("    Visited cluster ID: " + visited.getClusterID());
            System.err.println("    Path: " + path);
            System.err.println("    All branch ends: " + allBranchEnds);
            */


            if (allBranchEnds.size() == path.size()) {
                // side step was made
                lastElementOf(allBranchEnds).clear();
                // System.err.println("Side step, remove last branchends");
            } else if (allBranchEnds.size() == path.size() + 1) {
                // ascend step was mafe
                removeLastElementOf(allBranchEnds);
                // System.err.println("Ascend");
            } else {
                while (allBranchEnds.size() < path.size()) {
                    // Descend step was made
                    // No need to connect anything
                    allBranchEnds.add(new ArrayList<>());
                    // System.err.println("Descend");
                }
            }

            // System.err.println(allBranchEnds);

            if (allBranchEnds.size() != path.size()) {
                throw new IllegalStateException();
            }

            // The y positions of branches of the right side of this cluster
            final List<Px2d> branchEnds = lastElementOf(allBranchEnds);


            // Render immediate leaves
            if (!visited.leaves().isEmpty()) {
                // has leaves
                final LeavesBlock l = new LeavesBlock(visited);
                l.paintLeaves(
                        layout.leavesArea.p0().plusy(height.getValue()),
                        paintLeafImage,
                        renderer,
                        branchEnds
                );

                height.add(l.pixelheight);
                height.add(this.leafGroupSeparation); // does not matter here
                // System.err.println(l.toString());
                po.worked(l.leafCount);
            }

            final Px2d newBranchEndpoint = this.clusterAreaRendering.paintCluster(visited, renderer, layout.clustersArea, branchEnds);

            // Branches drawn, no need to draw them again
            branchEnds.clear();


            // will need to draw a branch to this cluster
            if (allBranchEnds.size() > 1) {
                // MoreLists.elementBeforeLastOf(allBranchEnds).add(clusterImageArea.pLeftCenter());
                MoreLists.elementBeforeLastOf(allBranchEnds).add(newBranchEndpoint);
            }
        });

        return renderer;
    }

    /**
     * Layout and write to output.
     *
     * @param out Output to write image to
     * @param poRendering Observer to track progress of rendering <b>but not PNG encoding</b>.
     * @throws IOException propagated
     */
    public void writeToPngImage(OutputStream out, SubProgressObserver poRendering) throws IOException {
        final BufferedImageRendering rendering = new BufferedImageRendering(false);
        final BufferedImageRenderer renderer = renderTo(rendering, poRendering);
        renderer.writePngImage(out);
    }

    /**
     * Dendrogram rendering reflecting a level association.
     *
     * Typical use case is to represent a similarity based hierarchic clustering where clusters have a similarity
     * value associated.
     */
    private class LevelAwareClusterAreaRendering implements ClusterAreaRendering {

        final Function<IDBasedHierarchicCluster, Double> clusterlevels;

        final int horizontalSize;

        final double maxLevel;

        /**
         * Construct.
         *
         * @param horizontalSize Horizontal pixel size assigned for tree part
         * @param levels Level assigner to use
         */
        public LevelAwareClusterAreaRendering(int horizontalSize, IDBasedAssigner levels) {
            this.clusterlevels = levels.clusterLevelFunction();
            this.maxLevel = Math.max(levels.maxLevel(), 1e-3);
            this.horizontalSize = horizontalSize;
        }

        @Override
        public int horizontalPixelCount() {
            return this.horizontalSize;
        }

        @Override
        public Px2d paintCluster(IDBasedHierarchicCluster cluster, Renderer renderer, Area clustersArea, List<Px2d> childrenEndpoints) {
            // Location of cluster tree startpoint
            // Which is expected to be the middle of vertical bar
            final int d = (int) Math.round(horizontalSize * Math.min(this.clusterlevels.apply(cluster), this.maxLevel) / this.maxLevel);
            final int x;
            switch (hierarchy.preferredAlignment()) {
                case LEAF_ALIGNED:
                    x = horizontalSize - d;
                    break;
                case ROOT_ALIGNED:
                    x = d;
                    break;
                default:
                    throw new AssertionError("Unknown preferred alignment " + hierarchy.preferredAlignment());
            }

            final Px2d p = of(
                    clustersArea.x0() + x,
                    (firstElementOf(childrenEndpoints).y + lastElementOf(childrenEndpoints).y) / 2
            );
            paintClusterBranches.paint(p, childrenEndpoints, renderer);

            // for single member clusters paint a vertical line
            if (cluster.immediateDescendantsCount() == 1 && leafImageSize.y >= 3) {
                final int bh = Math.max(3, leafImageSize.y - 2);
                final Px2d vp0 = p.plusy(-(bh - 1)/ 2);
                final Px2d vp1 = vp0.plusy(bh - 1);
                renderer.drawLine(vp0, vp1);
            }

            return p;
        }

        @Override
        public Optional<SimplePane> createScaleArea() {
            return Optional.of(new SimplePane(horizontalSize, 25, Halign.LEFT, Valign.FILL, (renderer, area) -> {
                renderer.drawLine(area.pLowerLeft().plusy(-5), area.pLowerRight().plusy(-5));
                renderer.drawLine(area.pLowerLeft().plusy(-5), area.pLowerLeft().plusy(-10));
                renderer.drawLine(area.pLowerRight().plusy(-5), area.pLowerRight().plusy(-10));
                renderer.placeHorizontalTextInto("0.0", Halign.RIGHT, Valign.BOTTOM, area, 0, -10);
                renderer.placeHorizontalTextInto(String.format(Locale.ENGLISH, "%.1f", this.maxLevel), Halign.LEFT, Valign.BOTTOM, area, 0, -10);
            }));
        }

    }

    /**
     * Default rendering uses even spacing of clusters.
     *
     * Cluster images are painted for clusters; clusters and branches can be customized. Cluster images (levels) are
     * evenly distributed.
     */
    private class DefaultClusterAreaRendering implements ClusterAreaRendering {


        @Override
        public int horizontalPixelCount() {
            // X size of clusters area from hierarchy max height and cluster image size specs
            return hierarchy.maxHeight() * (clusterImageSize.x + clusterSeparation);
        }

        @Override
        public Px2d paintCluster(IDBasedHierarchicCluster visited, Renderer renderer, Area clustersArea, List<Px2d> branchEnds) {
            // Render cluster image
            // Position according to branch end
            // TODO: is it guaranteed that the branchy list contains values in an ascending order - seemingly yes
            final Area clusterImageArea = Area.ofRect(
                    Px2d.of(
                        visited.depth() * (clusterImageSize.x + clusterSeparation) + clustersArea.x0(),
                        (firstElementOf(branchEnds).y + lastElementOf(branchEnds).y - clusterImageSize.y + 1) / 2
                    ),
                    clusterImageSize);

            paintClusterImage.paint(visited, renderer, clusterImageArea);

            // Paint horizontal line between cluster image and cluster branches
            final Px2d sp = clusterImageArea.pRightCenter().plusx(clusterSeparation / 2);
            renderer.drawLine(clusterImageArea.pRightCenter(), sp);

            // Paint branches
            paintClusterBranches.paint(
                    sp,
                    branchEnds,
                    renderer);
            return clusterImageArea.pLeftCenter();
        }

        @Override
        public Optional<SimplePane> createScaleArea() {
            return Optional.absent();
        }

    }


    /**
     * Layout of the cluster rendering.
     *
     * Initialization of the members reflecting current rendering parameters is done in {@link #areaSpec()}.
     */
    private final class Layout implements SubPane {


        /**
         * Area of clusters.
         */
        SimplePane clustersArea = null;
        /**
         * Area of leaves.
         */
        SimplePane leavesArea = null;

        /**
         * Root grid.
         */
        GridPane rootGrid = null;

        /**
         * Scale area.
         */
        Optional<SimplePane> scaleArea = null;

        /**
         * Cluster area rendering.
         */
        final ClusterAreaRendering clusterAreaRendering;

        /**
         * Construct considering current parameters.
         */
        Layout(ClusterAreaRendering clusterAreaRendering) {
            this.clusterAreaRendering = clusterAreaRendering;
        }

        @Override
        public AreaSpec areaSpec() {
            // Layout
            //        0                      1                 2      3      4
            //      +--+                                      +--+
            //   0  |  |                                      |  |
            //      +--+--------------------------------------+--+
            //   1     |         Scale (optional)             |
            //         +--------------------------------------+  +---------+
            //   2     |         Clusters                     |  | Leaves  |
            //         :                                      :  :         :
            //         +--------------------------------------+  +---------+--+
            //   3                                                         |  |
            //                                                             +--+

            this.rootGrid = new GridPane();
            this.rootGrid.set(0, 0, new SimplePane(borderLeft, borderTop));
            this.rootGrid.set(2, 0, new SimplePane(clustersLeavesSeparation, borderTop));
            this.rootGrid.set(4, 3, new SimplePane(borderRight, borderBottom));


            // Collect height from leaves

            // Determine y size of the canvas
            // Need to place leaves
            // Which are possibly grouped
            // Collect height
            final MutableInt height = new MutableInt();
            // We could use either post or pre order DFS traversal here
            // Post order is chosen to be consistent with the traversal used later for rendering
            Util.traverseSimplePostOrderDfs(hierarchy, (path, visited) -> {

                if (visited.leaves().isEmpty()) {
                    return;
                }
                if (height.intValue() > 0) {
                    height.add(leafGroupSeparation);
                }
                height.add(new LeavesBlock(visited).pixelheight);
            });



            // Construct clusters area spec

            this.clustersArea = new SimplePane(clusterAreaRendering.horizontalPixelCount(), height.intValue());

            this.scaleArea = this.clusterAreaRendering.createScaleArea();
            if (this.scaleArea.isPresent()) {
                this.rootGrid.set(1, 1, scaleArea.get());
            }

            // Construct leaves area spec
            final int leavesHorizontalSize = leafImageSize.x * leafMaxCols + leafSeparation * (leafMaxCols - 1);
            this.leavesArea = new SimplePane(leavesHorizontalSize, height.intValue());

            // Add cluster/leaves area to grid
            this.rootGrid.set(1, 2, clustersArea);
            this.rootGrid.set(3, 2, leavesArea);

            return this.rootGrid.areaSpec();
        }

        @Override
        public void layout(Area parentArea) {
            this.rootGrid.layout(parentArea);
        }


    }

    /**
     * Represents the immediate leaves of a cluster.
     *
     * Leaves might be rendered in a single column or as a block. The connection of the leaves to their immediate
     * parent cluster is a single line in case of block rendering. For single column layout the leaves are connected
     * to the immediate parent individually.
     */
    private final class LeavesBlock {
        /**
         * Represented leaves count.
         *
         * Delegates {@link IDBasedHierarchicCluster#leaves()} {@link List#size()}.
         */
        final int leafCount;

        /**
         * Columns used.
         */
        final int leafCols;

        /**
         * Rows used.
         */
        final int leafRows;

        /**
         * Height of the block, excluding {@link DetailedClusteringRendering#leafGroupSeparation}.
         */
        final int pixelheight;

        /**
         * Associated cluster.
         */
        final IDBasedHierarchicCluster cluster;

        /**
         * Construct for a non empty cluster.
         *
         * @param cluster Cluster
         */
        LeavesBlock(IDBasedHierarchicCluster cluster) {
            if (cluster.leaves().isEmpty()) {
                throw new IllegalStateException("No leaves for cluster " + cluster);
            }
            this.cluster = cluster;
            this.leafCount = cluster.leaves().size();
            this.leafCols = this.leafCount >= leafMaxCols ? leafMaxCols : this.leafCount;
            this.leafRows = this.leafCount / this.leafCols + (this.leafCount % this.leafCols == 0 ? 0 : 1);
            this.pixelheight = this.leafRows * leafImageSize.y + (this.leafRows - 1 ) * leafSeparation;


        }

        @Override
        public String toString() {
            return "Cluster ID: " + this.cluster.getClusterID()
                    + ", leafCount: " + this.leafCount
                    + ", leafCols: " + this.leafCols
                    + ", leafRows: " + this.leafRows
                    + ", pixelHeight: " + this.pixelheight;
        }



        /**
         * Paint leaves.
         *
         * @param p0 Upper left corner of area to occupy
         * @param paint Callback to paint individual leaves
         * @param renderer Renderer to paint to
         * @param branchEnds Endpoints of branches to be drawn
         */
        void paintLeaves(Px2d p0, PaintLeafImage paint, Renderer renderer, List<Px2d> branchEnds) {
            if (leafMaxCols > 1) {
                paintLeafGroupDecoration.paint(
                        renderer,
                        Area.ofRect(
                                p0.x,
                                p0.y,
                                leafImageSize.x * this.leafCols + leafSeparation * (this.leafCols - 1),
                                leafImageSize.y * this.leafRows + leafSeparation * (this.leafRows - 1)
                        ),
                        branchEnds);
                // Bind the entire group
                // Binding fone in callback
                // branchEnds.add(p0.plusx(-1).plusy(this.pixelheight / 2));
            }
            final List<Integer> leaves = this.cluster.leaves(); // Will need leaf IDs
            for (int j = 0; j < this.leafRows; j++) { // run through rows

                for (int i = 0; i < this.leafCols; i++) { // run through cols - inner loop must be cols
                    final int l = i + j * this.leafCols; // cell index
                    if (l >= this.leafCount) {
                        // Ran out of leaves
                        break;
                    }
                    // Area to paint lead
                    final Area paintArea = Area.ofRect(
                            p0.plus(leafImageSize.mulx(i).muly(j)).plusx(i * leafSeparation).plusy(j * leafSeparation),
                            leafImageSize
                    );

                    if (i == 0 && this.leafCols == 1) {
                        // bind every lead columnt
                        branchEnds.add(paintArea.pLeftCenter().plusx(-1));
                    }

                    paint.paint(
                        leaves.get(l), // Get leaf id from cluster
                        renderer, // pass renderer
                        paintArea // Area to paint
                    );
                }
            }
        }
    }


    /**
     * Specify level aware rendering.
     *
     * Note that cluster image rendering optionally specified by
     * {@link #clusterImage(com.chemaxon.clustering.cli.PaintClusterImage)} is ignored. Parameter
     * {@link #clusterImageSize(com.chemaxon.overlap.cli.util.images.Px2d)} is thus also ignored.
     *
     * @param clusterAreaSize Size of clusters area in pixels
     * @param levels Levels to use
     * @return reference to {@code this} instance
     */
    public DetailedClusteringRendering levelAware(int clusterAreaSize, IDBasedAssigner levels) {
        this.clusterAreaRendering = new LevelAwareClusterAreaRendering(clusterAreaSize, levels);
        return this;
    }



    /**
     * Specify cluster image painting.
     *
     * Ignored for level aware rendering specified by
     * {@link #levelAware(int, com.chemaxon.clustering.common.IDBasedAssigner)}.
     *
     * @param paintClusterImage Cluster image painter
     * @return Reference to this instance
     */
    public DetailedClusteringRendering clusterImage(PaintClusterImage paintClusterImage) {
        this.paintClusterImage = paintClusterImage;
        return this;
    }

    /**
     * Specify cluster branches rendering.
     *
     * @param paintClusterBranches Cluster branches painter
     * @return Reference to this instance
     */
    public DetailedClusteringRendering branches(PaintClusterBranches paintClusterBranches) {
        this.paintClusterBranches = paintClusterBranches;
        return this;
    }

    /**
     * Specify leaf image painting.
     *
     * @param paintLeafImage Leaf image painter
     * @return Reference to this instance
     */
    public DetailedClusteringRendering leafImage(PaintLeafImage paintLeafImage) {
        this.paintLeafImage = paintLeafImage;
        return this;
    }

    /**
     * Border left.
     *
     * @param borderLeft Left border in pixels
     * @return Reference to this instance
     */
    public DetailedClusteringRendering borderLeft(int borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    /**
     * Border right.
     *
     * @param borderRight Right border in pixels
     * @return Reference to this instance
     */
    public DetailedClusteringRendering borderRight(int borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    /**
     * Border top.
     *
     * @param borderTop Top border in pixels
     * @return Reference to this instance
     */
    public DetailedClusteringRendering borderTop(int borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    /**
     * Border bottom.
     *
     * @param borderBottom Bottom border in pixels
     * @return Reference to this instance
     */
    public DetailedClusteringRendering borderBottom(int borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    /**
     * Set leaf image size.
     *
     * @param leafImageSize Rendering area size for leaves
     * @return Reference to this instance
     */
    public DetailedClusteringRendering leafImageSize(Px2d leafImageSize) {
        this.leafImageSize = leafImageSize;
        return this;
    }

    /**
     * Set cluster image size.
     *
     * Ignored for level aware rendering specified by
     * {@link #levelAware(int, com.chemaxon.clustering.common.IDBasedAssigner)}.
     *
     * @param clusterImageSize Rendering area size for clusters
     * @return Reference to this instance
     */
    public DetailedClusteringRendering clusterImageSize(Px2d clusterImageSize) {
        this.clusterImageSize = clusterImageSize;
        return this;
    }


    /**
     * Set space between leaf image areas of the same cluster.
     *
     * @param leafSeparation Leaf separation
     * @return Reference to this instance
     */
    public DetailedClusteringRendering leafSeparation(int leafSeparation) {
        this.leafSeparation = leafSeparation;
        return this;
    }

    /**
     * Set space between leaf image areas of different clusters
     *
     * @param leafGroupSeparation Leaf separation
     * @return Reference to this instance
     */
    public DetailedClusteringRendering leafGroupSeparation(int leafGroupSeparation) {
        this.leafGroupSeparation = leafGroupSeparation;
        return this;
    }

    /**
     * Set leaf grouping column count.
     *
     * @param leafMaxCols Number of leaves to print in a row
     * @return Reference to this instance
     */
    public DetailedClusteringRendering leafMaxCols(int leafMaxCols) {
        this.leafMaxCols = leafMaxCols;
        return this;
    }
}
