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

package com.chemaxon.clustering.web.dao;

import com.chemaxon.clustering.web.entities.Molfile;
import com.chemaxon.clustering.web.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Repository;

/**
 * Provides access to {@link Molfile} instances.
 *
 * Current implementation provides in-memory storage with no further persistence.
 *
 * @author Gabor Imre
 */
@Repository
public class MolfilesDao {

    /**
     * Stored data.
     */
    private final BiMap<String, Molfile> storage;

    /**
     * Construct.
     */
    public MolfilesDao() {
        this.storage = HashBiMap.<String, Molfile>create();
    }

    /**
     * Add a new instance.
     *
     * @param idSuggestion ID suggestion. When already in use an added {@code -<NUMBER>} suffix is appended.
     * @param molfile Instance to add.
     * @return Associated ID
     * @throws IllegalArgumentException when the specified ID is already used.
     */
    public synchronized String add(String idSuggestion, Molfile molfile) {
        final String idToUse = Util.constructUniqueKey(this.storage, idSuggestion);
        this.storage.put(idToUse, molfile);
        return idToUse;
    }


    /**
     * Retrieve a stored instance.
     *
     * @param id Instance ID.
     * @return instance
     * @throws NoSuchElementException when no instance with the specified id found.
     */
    public synchronized Molfile get(String id) {
        Preconditions.checkNotNull(id);
        if (!this.storage.containsKey(id)) {
            throw new NoSuchElementException("Molfile ID not found " + id);
        }
        return this.storage.get(id);
    }


    /**
     * Retrieve the ID of a stored instance.
     *
     * @param molfile Instance previously added with {@link #add(java.lang.String, com.chemaxon.clustering.Molfile)}
     * @return Instance ID
     * @throws NoSuchElementException when instance not found
     */
    public synchronized String getIdOf(Molfile molfile) {
        Preconditions.checkNotNull(molfile);
        if (!this.storage.inverse().containsKey(molfile)) {
            throw new NoSuchElementException("Item not found: " + molfile);
        }
        return this.storage.inverse().get(molfile);
    }


    /**
     * Retrieve all stored instances.
     *
     * @return All stored instances, keyed by ID
     */
    public synchronized Map<String, Molfile> getAll() {
        return ImmutableMap.copyOf(this.storage);
    }
}
