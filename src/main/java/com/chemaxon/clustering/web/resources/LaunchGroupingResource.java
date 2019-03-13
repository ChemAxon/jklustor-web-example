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

import com.chemaxon.clustering.web.dto.GroupingInfo;
import com.chemaxon.clustering.web.entities.Grouping;
import com.chemaxon.clustering.web.entities.Molfile;
import com.chemaxon.clustering.web.services.GroupingService;
import com.chemaxon.clustering.web.services.MolfilesService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * REST API endpoints for launching grouping (non-hierarchical / single level clustering) calculations.
 *
 * @author Gabor Imre
 */
@Component
@Path("/launch-grouping")
public class LaunchGroupingResource {

    @Autowired
    private MolfilesService molfilesService;

    @Autowired
    private GroupingService groupingService;

    @Autowired
    private GroupingResource groupingResource;

    /**
     * Invoke random clustering.
     *
     * @param molfileId Structures to cluster
     * @param count Max cluster count
     * @param resnameSuggestion Resource name suggestion for the result
     * @return Grouping info
     */
    @POST
    @Path("invoke-random-clustering-on-molfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GroupingInfo invokRandomClusteringOnMolfile(
            @FormParam("molfile") String molfileId,
            @FormParam("count") @DefaultValue("10") int count,
            @FormParam("resname") String resnameSuggestion
    ) {
        if (molfileId == null) {
            throw new IllegalArgumentException("No molfile specified");
        }
        final Molfile molfile = this.molfilesService.getMolfile(molfileId);

        if (count <= 0) {
            throw new IllegalArgumentException("No or invalid count specified: " + count);
        }

        if (resnameSuggestion == null || resnameSuggestion.isEmpty()) {
            resnameSuggestion = this.molfilesService.getMolfileId(molfile) + "-rnd-grp-" + count;
        }


        final Grouping grp = this.groupingService.invokeRandomClustering(
            molfile,
            count,
            resnameSuggestion
        );
        return this.groupingResource.groupingInfo(grp);

    }


    /**
     * Invoke random selection.
     *
     * @param molfileId Structures to cluster
     * @param count Max element count to select
     * @param resnameSuggestion  Resource name suggestion for the result
     * @return Grouping info
     */
    @POST
    @Path("invoke-random-selection-on-molfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GroupingInfo invokRandomSelectionOnMolfile(
            @FormParam("molfile") String molfileId,
            @FormParam("count") @DefaultValue("100") int count,
            @FormParam("resname") String resnameSuggestion
    ) {
        if (molfileId == null) {
            throw new IllegalArgumentException("No molfile specified");
        }
        final Molfile molfile = this.molfilesService.getMolfile(molfileId);

        if (count <= 0) {
            throw new IllegalArgumentException("No or invalid count specified: " + count);
        }

        if (resnameSuggestion == null || resnameSuggestion.isEmpty()) {
            resnameSuggestion = this.molfilesService.getMolfileId(molfile) + "-rnd-sel-" + count;
        }


        final Grouping grp = this.groupingService.invokeRandomSelection(
            molfile,
            count,
            resnameSuggestion
        );
        return this.groupingResource.groupingInfo(grp);

    }


    /**
     * Invoke sphere exclusion centroid filtering.
     *
     * Molecules referenced by single group are filtered. The resulting simple group contains only those references which
     * are separated by the specified radius.
     *
     * @param molfileId Associated molfile
     * @param groupingId Source grouping where members refer to the associated molfile
     * @param groupIndex Group index from the source grouping
     * @param radius A dissimilarity radius
     * @param resnameSuggestion  Resource name suggestion for the result
     * @return The filtered grouping
     */
    @POST
    @Path("invoke-sphex-centroid-filtering")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GroupingInfo invokeSphexCentroidFiltering(
        @FormParam("molfile") String molfileId,
        @FormParam("grouping") String groupingId,
        @FormParam("groupindex") @DefaultValue("0") int groupIndex,
        @FormParam("radius") @DefaultValue("0.1") double radius,
        @FormParam("resname") String resnameSuggestion
    ) {
        if (molfileId == null) {
            throw new IllegalArgumentException("No molfile specified");
        }
        if (groupingId == null) {
            throw new IllegalArgumentException("No grouping specified");
        }

        final Molfile molfile = this.molfilesService.getMolfile(molfileId);
        final Grouping srcgrp = this.groupingService.getGrouping(groupingId);

        if (resnameSuggestion == null || resnameSuggestion.isEmpty()) {
            resnameSuggestion = groupingId + ":" + groupIndex + "-filt-r-" + radius;
        }


        final Grouping grp = this.groupingService.invokeSphexCentroidFilter(
            srcgrp,
            molfile,
            groupIndex,
            radius,
            resnameSuggestion
        );

        return this.groupingResource.groupingInfo(grp);
    }

    /**
     * Invoke nearest neighbor association.
     *
     * Centroids referenced by a single group will be used to assign the molecules in a referenced molfile into
     * clusters.
     *
     * @param molfileId Associated molfile
     * @param groupingId Source grouping where members refer to the associated molfile; memebers will be used as centroids
     * @param groupIndex Group index from the source grouping
     * @param resnameSuggestion  Resource name suggestion for the result
     * @return The filtered grouping
     */
    @POST
    @Path("invoke-nearest-neighbor-association")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GroupingInfo invokeNearestNeighborAssociation(
        @FormParam("molfile") String molfileId,
        @FormParam("grouping") String groupingId,
        @FormParam("groupindex") @DefaultValue("0") int groupIndex,
        @FormParam("resname") String resnameSuggestion
    ) {
        if (molfileId == null) {
            throw new IllegalArgumentException("No molfile specified");
        }
        if (groupingId == null) {
            throw new IllegalArgumentException("No grouping specified");
        }

        final Molfile molfile = this.molfilesService.getMolfile(molfileId);
        final Grouping srcgrp = this.groupingService.getGrouping(groupingId);

        if (resnameSuggestion == null || resnameSuggestion.isEmpty()) {
            resnameSuggestion = molfile + "-nn-by-" + groupingId + ":" + groupIndex;
        }

        final Grouping grp = this.groupingService.invokeNearestNeighborAssociation(
            srcgrp,
            molfile,
            groupIndex,
            resnameSuggestion
        );

        return this.groupingResource.groupingInfo(grp);
    }

}
