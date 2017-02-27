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

package com.chemaxon.clustering.web.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * Simple DTO to represent uploaded molfile.
 *
 * @author Gabor Imre
 */
@XmlRootElement
@SuppressFBWarnings(
    value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD",
    justification = "Fields of this DTO is read by JSON serialization."
)
public class MolfileInfo {


    /**
     * Original file name.
     *
     * Value from {@link FormDataContentDisposition#getFileName()} or an empty String ({@code ""}).
     */
    @XmlElement(required = true)
    public String originalFileName;

    /**
     * Original uploaded file size in bytes.
     */
    @XmlElement(required = true)
    public long originalFileSize;

    /**
     * Represented molecule count.
     */
    @XmlElement(required = true)
    public long moleculeCount;


    /**
     * ID of the molfile.
     */
    @XmlElement(required = true)
    public String id;

    /**
     * URL of the molfile.
     */
    @XmlElement(required = true)
    public String url;
}
