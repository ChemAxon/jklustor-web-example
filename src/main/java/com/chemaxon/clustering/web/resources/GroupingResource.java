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

import com.chemaxon.clustering.web.dto.Deleted;
import com.chemaxon.clustering.web.dto.GroupingInfo;
import com.chemaxon.clustering.web.dto.GroupingsInfo;
import com.chemaxon.clustering.web.dto.NonhierarchicClustering;
import com.chemaxon.clustering.web.entities.Grouping;
import com.chemaxon.clustering.web.services.GroupingService;
import java.util.stream.Collectors;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Query non-hierarchic (single level) clustering representations.
 *
 * @author Gabor Imre
 */
@Component
@Path("/groupings")
public class GroupingResource {

    @Autowired
    private GroupingService groupingService;

    /**
     * List available groupings.
     *
     * @return List of available groupings
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GroupingsInfo listGroupings() {
        final GroupingsInfo ret = new GroupingsInfo();
        ret.groupings = this.groupingService.getAllGroupings().values().stream().map(this::groupingInfo).collect(Collectors.toList());
        return ret;
    }

    /**
     * Get non-hierarchic clustering details.
     *
     * @param grouping Grouping
     * @return Details
     */
    @GET
    @Path("{grouping}/all")
    @Produces(MediaType.APPLICATION_JSON)
    public NonhierarchicClustering getGrouping(
            @PathParam("grouping") Grouping grouping

    ) {
        final NonhierarchicClustering ret = new NonhierarchicClustering();
        ret.setClustering(grouping.getGrouping());

        ret.algorithmDescription = grouping.getAlgorithmDescription();
        ret.elapsedTime = grouping.getElapsedTime();
        ret.id = this.groupingService.getGroupingId(grouping);
        ret.url = "/rest/groupings/" + ret.id;

        return ret;
    }

    /**
     * Basic description of a grouping.
     *
     * @param grouping Grouping to describe
     * @return Description
     */
    @GET
    @Path("{grouping}")
    @Produces(MediaType.APPLICATION_JSON)
    public GroupingInfo groupingInfo(
            @PathParam("grouping") Grouping grouping
    ) {
        final String id = this.groupingService.getGroupingId(grouping);
        final GroupingInfo ret = new GroupingInfo();
        ret.id = id;
        ret.url = "/rest/groupings/" + ret.id;
        ret.algorithmDescription = grouping.getAlgorithmDescription();
        ret.elapsedTime = grouping.getElapsedTime();

        return ret;
    }

    /**
     * Delete a grouping.
     *
     * @param grouping Grouping to delete
     * @return Should be ignored
     */
    @DELETE
    @Path("{grouping}")
    @Produces(MediaType.APPLICATION_JSON)
    public Deleted deleteGrouping(
        @PathParam("grouping") Grouping grouping
    ) {
        this.groupingService.deleteGrouping(grouping);
        return new Deleted();
    }



}
