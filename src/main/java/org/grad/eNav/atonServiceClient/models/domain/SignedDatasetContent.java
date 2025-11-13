/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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
 */

package org.grad.eNav.atonServiceClient.models.domain;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * The type Signed dataset content.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SignedDatasetContent {

    // Class Variables
    private String signedBy;
    private String issuedBy;
    private byte[] content;

    /**
     * Gets signed by.
     *
     * @return the signed by
     */
    public String getSignedBy() {
        return signedBy;
    }

    /**
     * Sets signed by.
     *
     * @param signedBy the signed by
     */
    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    /**
     * Gets issued by.
     *
     * @return the issued by
     */
    public String getIssuedBy() {
        return issuedBy;
    }

    /**
     * Sets issued by.
     *
     * @param issuedBy the issued by
     */
    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Gets content as a string.
     *
     * @return the content as a string
     */
    public String getContentAsString() {
        return new String(this.content, StandardCharsets.UTF_8);
    }
}
