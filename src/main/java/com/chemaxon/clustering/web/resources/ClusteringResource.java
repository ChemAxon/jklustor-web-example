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
package com.chemaxon.clustering.web.resources;

import com.chemaxon.clustering.web.dto.ClusteringInfo;
import com.chemaxon.clustering.web.dto.ClusteringsInfo;
import com.chemaxon.clustering.web.dto.Deleted;
import com.chemaxon.clustering.web.dto.HierarchicClustering;
import com.chemaxon.clustering.web.entities.Clustering;
import com.chemaxon.clustering.web.services.ClusteringService;
import com.chemaxon.clustering.cli.DetailedClusteringRendering;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.chemaxon.calculations.common.ProgressObservers;
import java.io.IOException;


/**
 * Query hierarchic clustering results.
 *
 * Invoking clustering is provided by {@link LaunchClusteringResource}. The URL pattern of
 * {@code /.../clusterings/{clustering_id}/...} is used.
 *
 * @author Gabor Imre
 */
@Component
@Path("/clusterings")
public class ClusteringResource {

    @Autowired
    private ClusteringService clusteringService;


    /**
     * List available clusterings.
     *
     * @return List of available clusterings
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ClusteringsInfo listClusterings() {
        final ClusteringsInfo ret = new ClusteringsInfo();
        ret.clusterings = Lists.transform(
                ImmutableList.copyOf(this.clusteringService.getAllClusterings().values())
                ,
                new Function<Clustering, ClusteringInfo>() {
                    @Override
                    public ClusteringInfo apply(Clustering input) {
                        return clusteringInfo(input);
                    }
                }
        );
        return ret;
    }

    /**
     * Get clustering hierarchy description.
     *
     * @param clustering Clustering
     * @return Hierarchic description
     */
    @GET
    @Path("{clustering}/hierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    public HierarchicClustering getHierarchicClustering(
            @PathParam("clustering") Clustering clustering
    ) {
    final HierarchicClustering ret = new HierarchicClustering();
        ret.setClustering(clustering.getClustering(), clustering.getAssigner());

        ret.algorithmDescription = clustering.getAlgorithmDescription();
        ret.elapsedTime = clustering.getElapsedTime();
        ret.id = this.clusteringService.getClusteringId(clustering);
        ret.url = "/rest/clusterings/" + ret.id;

        return ret;
    }

    
    @GET
    @Path("{clustering}/hierarchy-image")
    @Produces("image/png")
    public byte[] getClusteringHierarchyAsPng(
            @PathParam("clustering") Clustering clustering
    ) throws IOException {
        final DetailedClusteringRendering rendering = new DetailedClusteringRendering(clustering.getClustering())
                .levelAware(800, clustering.getAssigner());
        /*
                .leafImageSize(of(10, 10))
                .leafImage((leafid, renderer, area) -> renderer
                        .setFontHeight(area.sy())
                        .setColor("#000000")
                        .placeHorizontalTextInto(input.getMolecule(leafid).getName(), Halign.LEFT, Valign.FILL, area, 0, 0)

                )
                .writeToPngImage(out, outpo);
        */
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            rendering.writeToPngImage(baos, ProgressObservers.nullProgressObserver());
            return baos.toByteArray();
        } 
    }
    
    
    /**
     * Basic description of a clustering.
     *
     * @param clustering Clustering to describe
     * @return Description
     */
    @GET
    @Path("{clustering}")
    @Produces(MediaType.APPLICATION_JSON)
    public ClusteringInfo clusteringInfo(
            @PathParam("clustering") Clustering clustering
    ) {
        final String id = this.clusteringService.getClusteringId(clustering);
        final ClusteringInfo ret = new ClusteringInfo();
        ret.id = id;
        ret.url = "/rest/clusterings/" + ret.id;
        ret.algorithmDescription = clustering.getAlgorithmDescription();
        ret.elapsedTime = clustering.getElapsedTime();
        return ret;
    }

    /**
     * Delete a clustering.
     *
     * @param clustering Clustering to delete
     * @return Should be ignored
     */
    @DELETE
    @Path("{clustering}")
    @Produces(MediaType.APPLICATION_JSON)
    public Deleted deleteClustering(
        @PathParam("clustering") Clustering clustering
    ) {
        this.clusteringService.deleteClustering(clustering);
        return new Deleted();
    }


}
