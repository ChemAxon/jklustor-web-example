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

import chemaxon.formats.MolExporter;
import chemaxon.struc.Molecule;
import com.chemaxon.clustering.web.entities.Molfile;
import com.chemaxon.clustering.web.services.MolfilesService;
import com.chemaxon.clustering.web.application.Status;
import com.chemaxon.clustering.web.dto.MolfileInfo;
import com.chemaxon.clustering.web.dto.MolfilesInfo;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Simple molecule files REST resource.
 *
 * Expose molecule files over REST.
 *
 * @author Gabor Imre
 */
@Component
@Path("/molfiles")
public class MolfilesResource {

    @Autowired
    private MolfilesService molfilesService;

    /**
     * List available molfiles.
     *
     * @return List of available molfiles
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MolfilesInfo listMolfiles() {
        final MolfilesInfo ret = new MolfilesInfo();
        ret.molfiles = Lists.transform(
                ImmutableList.copyOf(this.molfilesService.getAllMolfiles().values())
                ,
                new Function<Molfile, MolfileInfo>() {
                    @Override
                    public MolfileInfo apply(Molfile input) {
                        return molfileInfo(input);
                    }
                }
        );
        return ret;
    }

    /**
     * Upload a structure file.
     *
     * @param uploadedInputStream {@code InputStream} to read structure data
     * @param fileDetail Details containing original uploaded file name
     * @return File info
     * @throws IOException propagated
     */
    // see http://stackoverflow.com/questions/25797650/fileupload-with-jaxrs - using Jersey to handle uploaded file
    // see http://stackoverflow.com/questions/4687271/jax-rs-how-to-return-json-and-http-status-code-together - returning 201 CREATED instead of 200 OK on success
    @POST
    @Status(Status.CREATED) // on success override 200 OK with 201 CREATED by StatusFilter
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public MolfileInfo uploadMolfile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail
    ) throws IOException {
        final byte [] uploaded = IOUtils.toByteArray(uploadedInputStream);
        final Molfile added = this.molfilesService.addMolfile(fileDetail.getFileName(), uploaded);
        return molfileInfo(added);
    }


    /**
     * Construct {@code Molfile} description.
     *
     * @param molfile Item to describe
     * @return Item description
     * @throws NoSuchElementException propagated when item not found
     */
    @GET
    @Path("{molfile}")
    @Produces(MediaType.APPLICATION_JSON)
    public MolfileInfo molfileInfo(
            @PathParam("molfile") Molfile molfile
    ) {
        final String id = this.molfilesService.getMolfileId(molfile);

        final MolfileInfo ret = new MolfileInfo();
        ret.originalFileName = molfile.getOriginalFilename().or("");
        ret.originalFileSize = molfile.getFileSizeInBytes();

        ret.id = id;
        ret.url = "/rest/molfiles/" + id;

        return ret;
    }


    /**
     * Download a molecule in SDF format.
     *
     * @param molfile Molfile to access
     * @param index Molecule index to access
     * @return Structure file in SDF format
     * @throws IOException propagated
     */
    @GET
    @Path("{molfile}/{index}/sdf")
    @Produces("chemical/x-mdl-sdfile")
    public Response getSdf(
            @PathParam("molfile") Molfile molfile,
            @QueryParam("index") long index
    ) throws IOException {
        final Molecule mol = molfile.getMolecule(index);
        final String sdf = MolExporter.exportToFormat(mol, "sdf");
        return Response
                .ok(sdf)
                .type("chemical/x-mdl-sdfile")
                .header("content-disposition", "attachment; filename=\"file.sdf\"")
                .build();

    }

    /**
     * Download a molecule in SMILES format.
     *
     * @param molfile Molfile to access
     * @param index Molecule index to access
     * @return Structure file in SMILES format
     * @throws IOException propagated
     */
    @GET
    @Path("{molfile}/{index}/smiles")
    @Produces("chemical/x-daylight-smiles")
    public Response getSmiles(
            @PathParam("molfile") Molfile molfile,
            @QueryParam("index") long index
    ) throws IOException {
        final Molecule mol = molfile.getMolecule(index);
        final String sdf = MolExporter.exportToFormat(mol, "smiles");
        return Response
                .ok(sdf)
                .type("chemical/x-daylight-sdfile")
                .header("content-disposition", "attachment; filename=\"file.smi\"")
                .build();

    }


    /**
     * Get molecule image in PNG format.
     *
     * @param molfile Molfile to use
     * @param index Structure index
     * @param w Image width in pixels
     * @param h Image height in pixels
     * @return Molecule 2D cleaned image
     * @throws IOException propagated from underlying {@link MolExporter}
     */
    @GET
    @Path("{molfile}/{index}/png")
    @Produces("image/png")
    public byte [] getPng(
        @PathParam("molfile") Molfile molfile,
        @PathParam("index") long index,
        @QueryParam("w") @DefaultValue("100") int w,
        @QueryParam("h") @DefaultValue("100") int h
    ) throws IOException {
        if (w < 1 || h < 1) {
            throw new IllegalArgumentException("Invalid image dimensions w: " + w + " h: " + h);
        }
        // Just grab the structure and convert to 2D on the fly
        // Caching, timeout, handling clean2d failure is not handled
        final Molecule mol = molfile.getMolecule(index);
        return MolExporter.exportToBinFormat(mol, "png:w" + w + "h" + h);
    }


}
