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
