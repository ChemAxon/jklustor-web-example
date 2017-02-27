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

package com.chemaxon.clustering.web.dao;

import com.chemaxon.clustering.web.Util;
import com.chemaxon.clustering.web.entities.Clustering;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Repository;

/**
 * Provides access to {@link Clustering} instances.
 *
 * Current implementation provides in-memory storage with no further persistence.
 *
 * @author Gabor Imre
 */
@Repository
public class ClusteringDao {

    /**
     * Stored data.
     */
    private final BiMap<String, Clustering> storage;

    /**
     * Construct.
     */
    public ClusteringDao() {
        this.storage = HashBiMap.<String, Clustering>create();
    }

    /**
     * Add a new instance.
     *
     * @param idSuggestion ID suggestion. When already in use an added {@code -<NUMBER>} suffix is appended.
     * @param clustering Instance to add.
     * @return Associated ID
     * @throws IllegalArgumentException when the specified ID is already used.
     */
    public synchronized String add(String idSuggestion, Clustering clustering) {
        final String idToUse = Util.constructUniqueKey(this.storage, idSuggestion);
        this.storage.put(idToUse, clustering);
        return idToUse;
    }


    /**
     * Retrieve a stored instance.
     *
     * @param id Instance ID.
     * @return instance
     * @throws NoSuchElementException when no instance with the specified id found.
     */
    public synchronized Clustering get(String id) {
        if (!this.storage.containsKey(id)) {
            throw new NoSuchElementException("Molfile ID not found " + id);
        }
        return this.storage.get(id);
    }


    /**
     * Retrieve the ID of a stored instance.
     *
     * @param clustering Instance previously added with {@link #add(java.lang.String, com.chemaxon.clustering.Clustering)}
     * @return Instance ID
     * @throws NoSuchElementException when instance not found
     */
    public synchronized String getIdOf(Clustering clustering) {
        if (!this.storage.inverse().containsKey(clustering)) {
            throw new NoSuchElementException("Item not found: " + clustering);
        }
        return this.storage.inverse().get(clustering);
    }


    /**
     * Retrieve all stored instances.
     *
     * @return All stored instances, keyed by ID
     */
    public synchronized Map<String, Clustering> getAll() {
        return ImmutableMap.copyOf(this.storage);
    }
}
