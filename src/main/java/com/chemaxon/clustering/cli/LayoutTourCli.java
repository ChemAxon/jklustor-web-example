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

import chemaxon.calculations.clean.Cleaner;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import com.chemaxon.calculations.common.ConsolePO;
import com.chemaxon.calculations.util.CmdlineUtils;
import com.chemaxon.clustering.common.HierarchicClustering;
import com.chemaxon.clustering.common.IDBasedHierarchicClusterBuidler;
import com.chemaxon.clustering.common.IDBasedHierarchicClustering;
import com.chemaxon.clustering.util.Util;
import com.chemaxon.overlap.cli.util.images.Area;
import com.chemaxon.overlap.cli.util.images.BufferedImageRenderer;
import com.chemaxon.overlap.cli.util.images.BufferedImageRendering;
import com.chemaxon.overlap.cli.util.images.GridPane;
import com.chemaxon.overlap.cli.util.images.Halign;
import com.chemaxon.overlap.cli.util.images.Px2d;
import static com.chemaxon.overlap.cli.util.images.Px2d.of;
import com.chemaxon.overlap.cli.util.images.SimplePane;
import com.chemaxon.overlap.cli.util.images.Valign;
import java.io.IOException;

/**
 * Demo of the simple layout/painting engine.
 *
 * Please note that the exposed engine has very limited functionality and it is still under development. Its classes,
 * API and API contracts are expected to undergo significant incompatible changes. 
 *
 * @author Gabor Imre
 */
public class LayoutTourCli {

    static void draw_simple() throws IOException {
        final BufferedImageRendering rendering = new BufferedImageRendering(false);
        final SimplePane pane =  rendering.simplePane(300, 200);
        final BufferedImageRenderer renderer = rendering.layout(pane);
        renderer.drawLine(0, 0, pane.sx - 1, pane.sy - 1);
        renderer.drawLine(0, pane.sy - 1, pane.sx - 1, 0);
        renderer.drawLine(10, 10, 20, 10);
        renderer.drawBorder(Area.ofRect(50, 50, 100, 50), 0);
        renderer.writePngImage(CmdlineUtils.outputStreamFromLocation("simple.png"));
    }

    static void draw_features() throws IOException {
        // Rendering is responsible for providing a drawable target after a layout
        // Note that rendering invokes paint for components created by it
        final BufferedImageRendering rendering = new BufferedImageRendering(false);

        // We use a grid layout
        // This component only collects size specification and assign positions for cells

        final GridPane grid = new GridPane();

        // Which is parametrized

        // The following specifications are used as placeholders, no actual drawing is done
        grid.set( // We will specify a cell in the grid
                0, 0, // In the upper left corner
                rendering.simplePane(10, 10) // which minimal size is 10px x 10px
        ); // an empty 10 x 10 px cell to set a border
        grid.set(2, 2, rendering.simplePane(10, 10)); // an empty 10 x 10 px cell to set a border
        grid.set(4, 4, rendering.simplePane(10, 10)); // an empty 10 x 10 px cell to set a border
        grid.set(4, 6, rendering.simplePane(10, 10)); // an empty 10 x 10 px cell to set a border
        grid.set(6, 6, rendering.simplePane(10, 10)); // an empty 10 x 10 px cell to set a border


        // Lets draw something
        grid.set( // We specify the area we want to paint
                1, // At column 1 in the grid
                1, // Row 1
                rendering.simplePane( // Will be a pane which
                    300, // Has a minimum 300 px width
                    200, // And 200 px height
                    (renderer, area) -> { // Which is painted upon layout by this callback
                        // This callback is invoked by the rendering after a layout
                        // Assigned a parent size and an actual location
                        // Renderer is created by the rendering and passed here
                        renderer // We use method chaining (like in builder pattern) for readability
                            .setColor("#DDDDDD") // set a light color
                            .fillArea(area, 0)
                            .setColor("#000000")
                            .drawEllipse(area);
                    } ));

        grid.set(3, 1, rendering.simplePane( 300, 200, (renderer, area) -> {
            renderer
                .setColor("#DDDDDD")
                .fillArea(area, 0)
                .setColor("#000000")
                .fillEllipse(area);
        }));

        grid.set(1, 3, rendering.simplePane( 300, 200, (renderer, area) -> {
            renderer
                .setColor("#DDDDDD")
                .fillArea(area, 0)
                .setColor("#000000")
                .drawBorder(area, 0)
                .setColor("#FF0000")
                .drawBorder(area, 2)
                .setColor("#0000FF")
                .drawBorder(area, -2);

        }));

        grid.set(3, 3, rendering.simplePane( 300, 200, (renderer, area) -> {
            renderer
                .setColor("#DDDDDD")
                .fillArea(area, 0)
                .setColor("#000000")
                .drawLine(area.pUpperLeft(), area.pLowerRight())
                .drawLine(area.pLowerLeft(), area.pUpperRight());
        }));


        grid.set(
                1, 5, // Specify cell col 1 row 5
                3, // Which spans 3 columns
                1, // And 1 row
                rendering.simplePane(
                        0, // Horizontal minimal size is 0 since colspan cells horizontal size is determined by the spanned columns
                        200, // But vertical minimal size is specified as usual
                        Halign.FILL, // Horizontally the pane must FILL the available space
                        Valign.TOP, // Vertical available size will be the minimal size
                        (renderer, area) -> { // like the painting callback
                            renderer
                                .setColor("#DDDDDD")
                                .fillArea(area, 0)
                                .setColor("#CCFFCC")
                                .drawLine(area.pLeftCenter(), area.pRightCenter())
                                .drawLine(area.pUpperCenter(), area.pLowerCenter())
                                .setColor("#000000")
                                .placeHorizontalTextInto("LEFT TOP horizontal", Halign.LEFT, Valign.TOP, area, 0, 0)
                                .placeHorizontalTextInto("RIGHT TOP horizontal", Halign.RIGHT, Valign.TOP, area, 0, 0)
                                .placeHorizontalTextInto("CENTER TOP horizontal", Halign.CENTER, Valign.TOP, area, 0, 0)
                                .placeHorizontalTextInto("LEFT CENTER horizontal", Halign.LEFT, Valign.CENTER, area, 0, 0)
                                .placeHorizontalTextInto("RIGHT CENTER horizontal", Halign.RIGHT, Valign.CENTER, area, 0, 0)
                                .placeHorizontalTextInto("CENTER CENTER horizontal", Halign.CENTER, Valign.CENTER, area, 0, 0)
                                .placeHorizontalTextInto("LEFT BOTTOM horizontal", Halign.LEFT, Valign.BOTTOM, area, 0, 0)
                                .placeHorizontalTextInto("RIGHT BOTTOM horizontal gjp", Halign.RIGHT, Valign.BOTTOM, area, 0, 0)
                                .placeHorizontalTextInto("CENTER BOTTOM horizontal", Halign.CENTER, Valign.BOTTOM, area, 0, 0);
                        }
                )
        );

        grid.set(5, 1, 1, 5, rendering.simplePane(200, 0, Halign.FILL, Valign.FILL, (renderer, area) -> {
            renderer
                    .setColor("#DDDDDD")
                    .fillArea(area, 0)
                    .setColor("#CCFFCC")
                    .drawLine(area.pLeftCenter(), area.pRightCenter())
                    .drawLine(area.pUpperCenter(), area.pLowerCenter())
                    .setColor("#000000")
                    .setFontHeight(30)
                    .placeVerticalTextInto("L TOP vert", Halign.LEFT, Valign.TOP, area, 0, 0)
                    .placeVerticalTextInto("RIGHT T vert", Halign.RIGHT, Valign.TOP, area, 0, 0)
                    .placeVerticalTextInto("C T vert", Halign.CENTER, Valign.TOP, area, 0, 0)
                    .placeVerticalTextInto("L C vert", Halign.LEFT, Valign.CENTER, area, 0, 0)
                    .placeVerticalTextInto("R C vert gjp", Halign.RIGHT, Valign.CENTER, area, 0, 0)
                    .placeVerticalTextInto("CENTER CENTER vert", Halign.CENTER, Valign.CENTER, area, 0, 0)
                    .placeVerticalTextInto("LEFT B vert", Halign.LEFT, Valign.BOTTOM, area, 0, 0)
                    .placeVerticalTextInto("R BOTTOM", Halign.RIGHT, Valign.BOTTOM, area, 0, 0)
                    .placeVerticalTextInto("C B vert", Halign.CENTER, Valign.BOTTOM, area, 0, 0);
        }));

        // Doing the layout
        // Layout assigns parent size, location and actual size for a root pane
        //  - Which is done by getting its minimal size and assigning it
        //  - Which in this case is a grid
        //      - Which determines its minimal size from the minimal size of its components
        //      - And assigns row heights, column widths
        //      - And passes them to the cells
        // After assignment rendering calls the paint callbacks of components created by it
        // Paint is not delegated by the component hierarchy
        final BufferedImageRenderer renderer = rendering.layout(grid);

        // And write image
        renderer.writePngImage(CmdlineUtils.outputStreamFromLocation("features.png"));
    }
    static void draw_molecule() throws IOException {
        final BufferedImageRendering rendering = new BufferedImageRendering(false);
        final SimplePane pane =  rendering.simplePane(300, 200);
        final BufferedImageRenderer renderer = rendering.layout(pane);

        final Area area = Area.ofRect(20, 10, 250, 150);
        // Border is outside of area
        renderer.setColor("#CCCCCC");
        renderer.drawBorder(area, 1);

        // Caffeine from https://en.wikipedia.org/wiki/Caffeine
        final Molecule mol = MolImporter.importMol("CN1C=NC2=C1C(=O)N(C(=O)N2C)C\tcaffeine");
        Cleaner.clean(mol, 2);
        renderer.drawMolecule(mol, area);


        renderer.writePngImage(CmdlineUtils.outputStreamFromLocation("molecule.png"));
    }


    static void draw_simplest_hierarchy_1() throws IOException {
        // Simplest possible hierachy
        final IDBasedHierarchicClusterBuidler builder =
                new IDBasedHierarchicClusterBuidler(HierarchicClustering.Alignment.LEAF_ALIGNED);
        builder.addStructureToCluster(0, 0);
        builder.setClusterRepresentant(0, 0);
        final IDBasedHierarchicClustering hierarchy = builder.build();

        System.err.println("Clustering:");
        System.err.println();
        System.err.println(Util.toMultilineString(hierarchy, "    "));
        System.err.println();

        final DetailedClusteringRendering clusterRendering = new DetailedClusteringRendering(hierarchy);
        clusterRendering.writeToPngImage(
                CmdlineUtils.outputStreamFromLocation("hierarchy-1.png"),
                new ConsolePO("Rendering clustering"));

        System.err.println();
    }

    static void draw_simplest_hierarchy_2() throws IOException {
        // Simplest possible hierachy
        final IDBasedHierarchicClusterBuidler builder =
                new IDBasedHierarchicClusterBuidler(HierarchicClustering.Alignment.LEAF_ALIGNED);
        builder.addStructureToCluster(0, 0);
        builder.addStructureToCluster(1, 0);
        builder.setClusterRepresentant(0, 0);
        final IDBasedHierarchicClustering hierarchy = builder.build();

        System.err.println("Clustering:");
        System.err.println();
        System.err.println(Util.toMultilineString(hierarchy, "    "));
        System.err.println();


        final DetailedClusteringRendering clusterRendering = new DetailedClusteringRendering(hierarchy);
        clusterRendering.writeToPngImage(
                CmdlineUtils.outputStreamFromLocation("hierarchy-2.png"),
                new ConsolePO("Rendering clustering"));

        System.err.println();
    }

    static void draw_simplest_hierarchy_3() throws IOException {

        final IDBasedHierarchicClustering hierarchy = new IDBasedHierarchicClusterBuidler(HierarchicClustering.Alignment.LEAF_ALIGNED)
                .newCluster().setRepresentant(0)
                    .newChildCluster().setRepresentant(0)
                        .addImmediateLeaf(0)
                        .addImmediateLeaf(1)
                .build();

        System.err.println("Clustering:");
        System.err.println();
        System.err.println(Util.toMultilineString(hierarchy, "    "));
        System.err.println();


        final DetailedClusteringRendering clusterRendering = new DetailedClusteringRendering(hierarchy);
        clusterRendering.writeToPngImage(
                CmdlineUtils.outputStreamFromLocation("hierarchy-3.png"),
                new ConsolePO("Rendering clustering"));

        System.err.println();
    }


    static void draw_simplest_hierarchy_4() throws IOException {

        final IDBasedHierarchicClustering hierarchy = new IDBasedHierarchicClusterBuidler(HierarchicClustering.Alignment.LEAF_ALIGNED)
                .newRootCluster().setRepresentant(0)
                    .addImmediateLeaf(2)
                    .newChildCluster().setRepresentant(0)
                        .addImmediateLeaf(0)
                        .addImmediateLeaf(1)
                .newRootCluster().setRepresentant(3)
                    .addImmediateLeaf(3)
                .build();

        System.err.println("Clustering:");
        System.err.println();
        System.err.println(Util.toMultilineString(hierarchy, "    "));
        System.err.println();


        final DetailedClusteringRendering clusterRendering = new DetailedClusteringRendering(hierarchy);
        clusterRendering.writeToPngImage(
                CmdlineUtils.outputStreamFromLocation("hierarchy-4.png"),
                new ConsolePO("Rendering clustering"));

        System.err.println();
    }

    static void draw_simplest_hierarchy_5() throws IOException {

        final IDBasedHierarchicClustering hierarchy = new IDBasedHierarchicClusterBuidler(HierarchicClustering.Alignment.LEAF_ALIGNED)
                .newRootCluster()
                    .setRepresentant(0)
                    .addImmediateLeaves(0, 1, 2, 3, 4, 5)
                    .newChildCluster()
                        .setRepresentant(6)
                        .addImmediateLeaves(6, 7, 8 , 9)
                    .newSiblingCluster()
                        .setRepresentant(21)
                        .addImmediateLeaves(21, 22, 23 , 24, 25, 26, 28)
                .newRootCluster().setRepresentant(10)
                    .addImmediateLeaf(10)
                    .newChildCluster()
                        .setRepresentant(11)
                        .addImmediateLeaves(11, 12)
                        .newChildCluster()
                            .setRepresentant(13)
                            .addImmediateLeaves(13, 14, 15)
                            .newChildCluster()
                                .setRepresentant(16)
                                .addImmediateLeaves(16, 17, 18, 19, 20)
                .build();

        System.err.println("Clustering:");
        System.err.println();
        System.err.println(Util.toMultilineString(hierarchy, "    "));
        System.err.println();


        final DetailedClusteringRendering clusterRendering = new DetailedClusteringRendering(hierarchy);
        clusterRendering.writeToPngImage(
                CmdlineUtils.outputStreamFromLocation("hierarchy-5-a.png"),
                new ConsolePO("Rendering clustering 5-a"));

        // Write cluster IDs above cluster symbols
        clusterRendering.clusterImage((cluster, renderer, area) -> {
            renderer
                    .setColor("#000000")
                    .fillEllipse(area)
                    .setFontHeight(area.sy())
                    .placeHorizontalTextInto(
                            Integer.toString(cluster.getClusterID()),
                            Halign.CENTER,
                            Valign.BOTTOM,
                            area,
                            0,
                            -area.sy() - 2);
        });

        clusterRendering.writeToPngImage(
                CmdlineUtils.outputStreamFromLocation("hierarchy-5-b.png"),
                new ConsolePO("Rendering clustering 5-b"));

        clusterRendering
                .borderRight(150)
                .leafImage((leaf, renderer, area) -> {
                    renderer
                            .setColor("#000000")
                            .drawEllipse(area)
                            .setFontHeight(area.sy())
                            .placeHorizontalTextInto(
                                    "Leaf " + leaf + " (" + Util.hierarchicIndexOf(leaf, hierarchy) + ")",
                                    Halign.LEFT,
                                    Valign.CENTER,
                                    area,
                                    area.sx() + 2,
                                    0);
                })
                .writeToPngImage(
                    CmdlineUtils.outputStreamFromLocation("hierarchy-5-c.png"),
                    new ConsolePO("Rendering clustering 5-c")
                );



        clusterRendering
                .branches((startpoint, endpoints, renderer) -> {
                    renderer.setColor("#000000");
                    for (Px2d p : endpoints) {
                        renderer.drawLine(startpoint, p);
                    }
                })
                .leafImageSize(of(11, 11))
                .clusterImageSize(of(15, 15))
                .leafSeparation(3)
                .leafGroupSeparation(7)
                .writeToPngImage(
                    CmdlineUtils.outputStreamFromLocation("hierarchy-5-d.png"),
                    new ConsolePO("Rendering clustering 5-d")
                );


        System.err.println();
    }

    static void draw_simplest_hierarchy_6() throws IOException {

        final IDBasedHierarchicClustering hierarchy = new IDBasedHierarchicClusterBuidler(HierarchicClustering.Alignment.LEAF_ALIGNED)
                .newRootCluster()
                    .setRepresentant(0)
                    .addImmediateLeaves(0, 1, 2, 3, 4, 5)
                    .newChildCluster()
                        .setRepresentant(6)
                        .addImmediateLeaves(6, 7, 8 , 9)
                    .newSiblingCluster()
                        .setRepresentant(21)
                        .addImmediateLeaves(21, 22, 23 , 24, 25, 26, 28)
                .newRootCluster().setRepresentant(10)
                    .addImmediateLeaf(10)
                    .newChildCluster()
                        .setRepresentant(11)
                        .addImmediateLeaves(11, 12)
                        .newChildCluster()
                            .setRepresentant(13)
                            .addImmediateLeaves(13, 14, 15)
                            .newChildCluster()
                                .setRepresentant(16)
                                .addImmediateLeaves(16, 17, 18, 19, 20)
                            .parent()
                        .parent()
                    .parent()
                    .newChildCluster()
                        .setRepresentant(29)
                        .addImmediateLeaves(29, 30, 31, 32, 33, 34, 35, 36, 37)

                .build();

        System.err.println("Clustering:");
        System.err.println();
        System.err.println(Util.toMultilineString(hierarchy, "    "));
        System.err.println();


        final DetailedClusteringRendering clusterRendering = new DetailedClusteringRendering(hierarchy);
        clusterRendering.writeToPngImage(
                CmdlineUtils.outputStreamFromLocation("hierarchy-6-a.png"),
                new ConsolePO("Rendering clustering 6-a"));

        clusterRendering
                .leafMaxCols(2)
                .writeToPngImage(
                    CmdlineUtils.outputStreamFromLocation("hierarchy-6-b.png"),
                    new ConsolePO("Rendering clustering 6-b")
                );

        clusterRendering
                .leafMaxCols(4)
                .writeToPngImage(
                    CmdlineUtils.outputStreamFromLocation("hierarchy-6-c.png"),
                    new ConsolePO("Rendering clustering 6-c")
                );

        clusterRendering
                .leafMaxCols(4)
                .leafGroupSeparation(5)
                .writeToPngImage(
                    CmdlineUtils.outputStreamFromLocation("hierarchy-6-d.png"),
                    new ConsolePO("Rendering clustering 6-d")
                );

        System.err.println();
    }


    static void draw_level_aware() throws IOException {

        final IDBasedHierarchicClustering hierarchy = new IDBasedHierarchicClusterBuidler(HierarchicClustering.Alignment.LEAF_ALIGNED)
                .newRootCluster()
                    .setLevel(3.0)
                    .setRepresentant(0)
                    .newChildCluster()
                        .setLevel(1.0)
                        .setRepresentant(0)
                            .addImmediateLeaves(0, 1)
                    .newSiblingCluster()
                        .setLevel(2.0)
                        .setRepresentant(2)
                        .addImmediateLeaves(2, 3)
                .newRootCluster()
                    .setLevel(5.0)
                    .setRepresentant(4)
                    .addImmediateLeaves(4, 5)
                .newRootCluster()
                    .setLevel(0.0)
                    .setRepresentant(6)
                    .addImmediateLeaves(6, 7, 8)
                .newRootCluster()
                    .setLevel(1.0)
                    .setRepresentant(9)
                    .addImmediateLeaves(9)
                .newRootCluster()
                    .setLevel(2.0)
                    .setRepresentant(10)
                    .newChildCluster()
                        .setRepresentant(10)
                        .setLevel(1.0)
                        .addImmediateLeaves(10)
                .build();

        System.err.println("Clustering:");
        System.err.println();
        System.err.println(Util.toMultilineString(hierarchy, "    "));
        System.err.println();


        final DetailedClusteringRendering clusterRendering = new DetailedClusteringRendering(hierarchy);
        clusterRendering
                .writeToPngImage(
                    CmdlineUtils.outputStreamFromLocation("hierarchy-7-a.png"),
                    new ConsolePO("Rendering clustering 7-a")

                );

        clusterRendering
                .levelAware(300, hierarchy.getPreferredAssigner())
                .writeToPngImage(
                    CmdlineUtils.outputStreamFromLocation("hierarchy-7-b.png"),
                    new ConsolePO("Rendering clustering 7-b")

                );
    }


    /**
     * Main method.
     *
     * @param args Currently no command line arguments exposed.
     */
    public static void main(String[] args) throws IOException {
        draw_simple();
        draw_molecule();
        draw_features();
        draw_simplest_hierarchy_1();
        draw_simplest_hierarchy_2();
        draw_simplest_hierarchy_3();
        draw_simplest_hierarchy_4();
        draw_simplest_hierarchy_5();
        draw_simplest_hierarchy_6();
        draw_level_aware();
    }
}
