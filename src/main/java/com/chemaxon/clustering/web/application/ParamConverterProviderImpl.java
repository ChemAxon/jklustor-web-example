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

package com.chemaxon.clustering.web.application;

import com.chemaxon.clustering.web.entities.Clustering;
import com.chemaxon.clustering.web.entities.Molfile;
import com.chemaxon.clustering.web.services.ClusteringService;
import com.chemaxon.clustering.web.services.MolfilesService;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides ID lookup for business objects to be used by the REST resources.
 *
 * @author Gabor Imre
 */
@Provider
public class ParamConverterProviderImpl implements ParamConverterProvider {
    // Note that services and converter implementations are handwired in the current implementation.
    // When more resources are implemented this implementation will be refactored to be more generic.

    /**
     * Converter to look up instances.
     */
    private ParamConverter<Molfile> molfileIdLookupParamConverter;

    /**
     * Converter to look up instances.
     */
    private ParamConverter<Clustering> clusteringIdLookupParamConverter;

    // Constructor injection is used to allow the usage of inner class converter
    @Autowired
    public ParamConverterProviderImpl(MolfilesService molfilesService, ClusteringService clusteringService) {
        this.molfileIdLookupParamConverter = new MolfileIdLookupParamConverter(molfilesService);
        this.clusteringIdLookupParamConverter = new ClusteringIdLookupParamConverter(clusteringService);
    }

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == Molfile.class) {
            return (ParamConverter<T>) this.molfileIdLookupParamConverter;
        } else if (rawType == Clustering.class) {
            return (ParamConverter<T>) this.clusteringIdLookupParamConverter;
        } else {
            return null;
        }
    }


    /**
     * Looking up instance by ID.
     */
    private static class MolfileIdLookupParamConverter implements ParamConverter<Molfile> {

        /**
         * Underlying service.
         */
        private final MolfilesService molfilesService;

        /**
         * Construct.
         *
         * @param molfilesService Underlying service
         */
        public MolfileIdLookupParamConverter(MolfilesService molfilesService) {
            this.molfilesService = molfilesService;
        }

        @Override
        public Molfile fromString(String value) {
            return this.molfilesService.getMolfile(value);
        }

        @Override
        public String toString(Molfile value) {
            return this.molfilesService.getMolfileId(value);
        }

    }


    /**
     * Looking up instance by ID.
     */
    private static class ClusteringIdLookupParamConverter implements ParamConverter<Clustering> {

        /**
         * Underlying service.
         */
        private final ClusteringService clusteringService;

        /**
         * Construct.
         *
         * @param clusteringService Underlying service
         */
        public ClusteringIdLookupParamConverter(ClusteringService clusteringService) {
            this.clusteringService = clusteringService;
        }

        @Override
        public Clustering fromString(String value) {
            return this.clusteringService.getClustering(value);
        }

        @Override
        public String toString(Clustering value) {
            return this.clusteringService.getClusteringId(value);
        }

    }


}
