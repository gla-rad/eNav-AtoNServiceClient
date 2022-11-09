/*
 * Copyright (c) 2022 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonServiceClient.components;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.grad.secom.core.base.DigitalSignatureCertificate;
import org.grad.secom.core.base.SecomSignatureProvider;
import org.grad.secom.core.models.enums.DigitalSignatureAlgorithmEnum;
import org.grad.secom.core.utils.SecomPemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;

/**
 * The SECOM Signature Validator Implementation.
 *
 * In the current e-Navigation Service Architecture, it's the cKeeper
 * microservice that is responsible for generating the validating the
 * SECOM message signatures.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Slf4j
public class SecomSignatureValidatorImpl implements SecomSignatureProvider {

    /**
     * The Application Name.
     */
    @Value("${gla.rad.aton-service-client.secom.signing-algorithm:SHA256withECDSA}")
    String defaultSigningAlgorithm;

    /**
     * Returns the digital signature algorithm for the signature validator.
     * In SECOM, by default this should be DSA, also ECDSA could be used
     * to generate smaller signatures.
     *
     * @return the digital signature algorithm for the signature provider
     */
    public DigitalSignatureAlgorithmEnum getSignatureAlgorithm() {
        return DigitalSignatureAlgorithmEnum.fromValue(this.defaultSigningAlgorithm);
    }

    /**
     * This function overrides the interface definition to link the SECOM
     * signature provision with the cKeeper operation. A service can request
     * cKeeper to sign a payload, using a valid certificate based on the
     * provided digital signature certificate information.
     *
     * @param signatureCertificate  The digital signature certificate to be used for the signature generation
     * @param algorithm             The algorithm to be used for the signature generation
     * @param payload               The payload to be signed, (preferably Base64 encoded)
     * @return The signature generated
     */
    @Override
    public byte[] generateSignature(DigitalSignatureCertificate signatureCertificate, DigitalSignatureAlgorithmEnum algorithm, byte[] payload) {
        throw new NotImplementedException("The AtoN Client does not have the capability of generating signatures");
    }

    /**
     * The signature validation operation. This should support the provision
     * of the message content (preferably in a Base64 format) and the signature
     * to validate the content against.
     *
     * @param signatureCertificate  The digital signature certificate to be used for the signature generation
     * @param algorithm             The algorithm used for the signature generation
     * @param signature             The signature to validate the context against
     * @param content               The context (in Base64 format) to be validated
     * @return whether the signature validation was successful or not
     */
    @Override
    public boolean validateSignature(String signatureCertificate, DigitalSignatureAlgorithmEnum algorithm, byte[] signature, byte[] content) {
        // Create a new signature to sign the provided content
        try {
            Signature sign = Signature.getInstance(algorithm.getValue());
            sign.initVerify(SecomPemUtils.getCertFromPem(signatureCertificate));
            sign.update(content);

            // Sign and return the signature
            return sign.verify(signature);
        } catch (NoSuchAlgorithmException | CertificateException | SignatureException | InvalidKeyException ex) {
            log.error(ex.getMessage());
            return false;
        }
    }
}
