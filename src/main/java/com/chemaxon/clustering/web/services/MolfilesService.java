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

package com.chemaxon.clustering.web.services;

import com.chemaxon.clustering.web.dao.MolfilesDao;
import com.chemaxon.clustering.web.entities.Molfile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides domain specific operations for MolFile instances.
 *
 * @author Gabor Imre
 */
@Service
public class MolfilesService {

    @Autowired
    private MolfilesDao molfilesDao;

    /**
     * Add molecule file content.
     *
     * @param originalFilename Original file name
     * @param content Content added.
     * @return Added molfile
     */
    public Molfile addMolfile(String originalFilename, byte [] content) {
        final InputStream is = new ByteArrayInputStream(content);
        try {
            final Molfile f = new Molfile(originalFilename, is);
            this.molfilesDao.add(originalFilename, f);
            return f;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Retrieve a molfile by ID.
     *
     * @param id ID of {@link Molfile} to retrieve
     * @return Instance identified by the given ID.
     * @throws NoSuchElementException when given ID not found.
     */
    public Molfile getMolfile(String id) {
        return this.molfilesDao.get(id);
    }


    /**
     * Retrieve all molfile instances.
     *
     * @return ID to molfile mapping.
     */
    public Map<String, Molfile>  getAllMolfiles() {
        return this.molfilesDao.getAll();
    }

    /**
     * Retrieve molfile ID.
     *
     * @param molfile Instance
     * @return ID of instance
     * @throws NoSuchElementException when given ID not found for the specified instance
     */
    public String getMolfileId(Molfile molfile) {
        return this.molfilesDao.getIdOf(molfile);
    }


    /**
     * Delete an existing molfile.
     *
     * @param molfile Instance
     */
    public void deleteMolfile(Molfile molfile) {
        this.molfilesDao.delete(molfile);
    }

    /**
     * Delete all molfiles.
     */
    public void deleteAllMolfiles() {
        this.molfilesDao.deleteAll();
    }

}
