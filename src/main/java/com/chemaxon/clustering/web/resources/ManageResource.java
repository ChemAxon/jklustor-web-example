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
import com.chemaxon.clustering.web.services.ClusteringService;
import com.chemaxon.clustering.web.services.GroupingService;
import com.chemaxon.clustering.web.services.MolfilesService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Basic management features.
 *
 * @author Gabor Imre
 */
@Component
@Path("/management")
public class ManageResource {

    @Autowired
    private MolfilesService molfilesService;

    @Autowired
    private GroupingService groupingService;

    @Autowired
    private ClusteringService clusteringService;

    @POST
    @Path("remove-all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Deleted removeAll() {
        this.molfilesService.deleteAllMolfiles();
        this.groupingService.deleteAllGroupings();
        this.clusteringService.deleteAllClusterings();

        return new Deleted();

    }


}
