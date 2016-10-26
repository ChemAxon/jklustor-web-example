/*
 * Copyright 2016 ChemAxon Ltd.
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

import com.chemaxon.clustering.web.entities.Clustering;
import com.chemaxon.clustering.web.services.ClusteringService;
import com.chemaxon.clustering.web.entities.Molfile;
import com.chemaxon.clustering.web.services.MolfilesService;
import com.chemaxon.clustering.web.dto.ClusteringInfo;
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
 * REST API endpoints for launching clustering calculations.
 *
 * @author Gabor Imre
 */
@Component
@Path("/launch-clustering")
public class LaunchClusteringResource {

    @Autowired
    private MolfilesService molfilesService;

    @Autowired
    private ClusteringService clusteringService;

    @Autowired
    private ClusteringResource clusteringResource;

    /**
     * Invoke Lance-Williams clustering on a molfile.
     *
     * @param molfileId Structures to cluster
     * @param algorithm Algorithm to use
     * @return Clustering info
     */
    @POST
    @Path("invoke-lance-williams-on-molfile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public ClusteringInfo invokeLanceWilliamsOnMolfile(
            //@QueryParam("molfile") Molfile molfile,
            @FormParam("molfile") String molfileId,
            @FormParam("algorithm") @DefaultValue("wards") LanceWilliamsAlgorithms algorithm
    ) {
        if (molfileId == null) {
            throw new IllegalArgumentException("No molfile specified");
        }
        final Molfile molfile = this.molfilesService.getMolfile(molfileId);

        if (algorithm == null) {
            throw new IllegalArgumentException("No algorithm specified");
        }

        final Clustering clus = this.clusteringService.invokeLanceWilliams(
                molfile, algorithm.getMerge(), this.molfilesService.getMolfileId(molfile) + "-" + algorithm);

        return this.clusteringResource.clusteringInfo(clus);
    }


}
