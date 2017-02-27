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

package com.chemaxon.clustering.web.entities;

import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CountingInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represent a flat molecule file.
 *
 * @author Gabor Imre
 */
public final class Molfile implements Serializable {

    /**
     * Original file name.
     *
     * In case of upload contains {@link MultipartFile#getOriginalFilename()}
     */
    private final Optional<String> originalFilename;

    /**
     * Structure file format.
     */
    private final String format;

    /**
     * Raw size in bytes.
     */
    private final long fileSizeInBytes;

    /**
     * Molecule objects.
     */
    private final ImmutableList<Molecule> molecules;

    /**
     * Construct.
     *
     * @param originalFilename Original file name to expose.
     * @param in Input to read from. Stream is <b>not</b> closed.
     */
    public Molfile(String originalFilename, InputStream in) {
        if (originalFilename == null || "".equals(originalFilename)) {
            this.originalFilename = Optional.<String>absent();
        } else {
            this.originalFilename = Optional.of(originalFilename);
        }

        final ImmutableList.Builder<Molecule> moleculesBuilder = new ImmutableList.Builder();

        try {
            final CountingInputStream cis = new CountingInputStream(in);
            final MolImporter mi = new MolImporter(cis);
            this.format = mi.getFormat();
            Molecule m;
            while ((m = mi.read()) != null) {
                moleculesBuilder.add(m);
            }
            this.fileSizeInBytes = cis.getCount();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error parsing molecules: " + e.getMessage(), e);
        }

        this.molecules = moleculesBuilder.build();
        if (this.molecules.isEmpty()) {
            throw new IllegalArgumentException("No structures added.");
        }
    }


    /**
     * Get all represented molecules.
     *
     * @return All represented molecules
     */
    public List<Molecule> getAllMolecules() {
        return this.molecules;
    }

    /**
     * Contained molecule count.
     *
     * @return Molecule count
     */
    public int size() {
        return this.molecules.size();
    }

    /**
     * Molecule format.
     *
     * @return Format string
     */
    public String format() {
        return this.format;
    }

    /**
     * Retrieve a stored molecule.
     *
     * @param index Molecule index
     * @return Stored molecule
     */
    public Molecule getMolecule(long index) {
        if (index > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid index " + index);
        }
        return this.molecules.get((int) index);
    }


    /**
     * Retrieve original file size in bytes.
     *
     * @return Original file size in bytes.
     */
    public long getFileSizeInBytes() {
        return this.fileSizeInBytes;
    }

    /**
     * Retrieve original file name if available.
     *
     * @return Original file name if available.
     */
    public Optional<String> getOriginalFilename() {
        return originalFilename;
    }


}
