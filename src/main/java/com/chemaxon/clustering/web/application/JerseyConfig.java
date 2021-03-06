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

package com.chemaxon.clustering.web.application;

import com.chemaxon.clustering.web.resources.ClusteringResource;
import com.chemaxon.clustering.web.resources.GroupingResource;
import com.chemaxon.clustering.web.resources.LaunchClusteringResource;
import com.chemaxon.clustering.web.resources.LaunchGroupingResource;
import com.chemaxon.clustering.web.resources.ManageResource;
import com.chemaxon.clustering.web.resources.MolfilesResource;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Configure Jersey JAX-RS server.
 *
 * @author Gabor Imre
 */
@Component
@ApplicationPath("rest")
public final class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerComponents();
    }

    private void registerComponents() {
        register(ManageResource.class);
        register(MolfilesResource.class);
        register(ClusteringResource.class);
        register(LaunchClusteringResource.class);
        register(GroupingResource.class);
        register(LaunchGroupingResource.class);
        register(ParamConverterProviderImpl.class);

        // See http://stackoverflow.com/questions/35644365/multipart-api-doesnt-work-in-jersey-with-springboot
        register(MultiPartFeature.class);

        // See http://stackoverflow.com/questions/4687271/jax-rs-how-to-return-json-and-http-status-code-together
        register(StatusFilter.class);
    }
}
