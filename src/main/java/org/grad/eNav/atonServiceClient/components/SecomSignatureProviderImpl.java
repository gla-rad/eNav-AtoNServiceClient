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

package org.grad.eNav.atonServiceClient.components;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.feign.CKeeperClient;
import org.grad.eNav.atonServiceClient.utils.X509Utils;
import org.grad.secomv2.core.base.DigitalSignatureCertificate;
import org.grad.secomv2.core.base.SecomSignatureProvider;
import org.grad.secomv2.core.models.enums.DigitalSignatureAlgorithmEnum;
import org.grad.secomv2.core.utils.SecomPemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

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
public class SecomSignatureProviderImpl implements SecomSignatureProvider {

    /**
     * The cKeeper Feign Client.
     */
    @Autowired
    @Lazy
    CKeeperClient cKeeperClient;

    /**
     * The Application Name.
     */
    @Value("${gla.rad.aton-service-client.secom.signing-algorithm:SHA3-384withECDSA}")
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
     * @param payload               The payload to be signed, (preferably Base64 encoded)
     * @return The signature generated
     */
    @Override
    public byte[] generateSignature(DigitalSignatureCertificate signatureCertificate, byte[] payload) {
        // Get the signing certificate signature algorithm
        DigitalSignatureAlgorithmEnum algorithm = DigitalSignatureAlgorithmEnum.fromValue(signatureCertificate.getCertificate()[0].getSigAlgName());
        // Get the signature generated from cKeeper
        final Response response = this.cKeeperClient.generateCertificateSignature(
                new BigInteger(signatureCertificate.getCertificateAlias()[0]),
                algorithm.getValue(),
                Optional.ofNullable(payload).orElse(new byte[]{}));

        // Make sure the response is valid
        if(response == null || response.body() == null) {
            return null;
        }

        // Parse the response
        try {
            return response.body().asInputStream().readAllBytes();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * The signature validation operation. This should support the provision
     * of the message content (preferably in a Base64 format) and the signature
     * to validate the content against.
     *
     * @param signatureCertificates The array of digital signature certificate to be used for the signature generation
     * @param algorithm             The algorithm used for the signature generation
     * @param signature             The signature to validate the context against
     * @param content               The context (in Base64 format) to be validated
     * @return whether the signature validation was successful or not
     */
    @Override
    public boolean validateSignature(String[] signatureCertificates, DigitalSignatureAlgorithmEnum algorithm, byte[] signature, byte[] content) {
        // Create a new signature to sign the provided content
        try {
            //TODO Get algorithm from the certificate

            Signature sign = Signature.getInstance(algorithm.getValue());

            for (String signatureCertificate: signatureCertificates){
                sign.initVerify(SecomPemUtils.getCertFromPem(signatureCertificate));
                sign.update(content);

                if (sign.verify(signature)) {
                    // If verified return true
                    return true;
                }

            }

            // If no certificates verified, return false
            return false;

        } catch (NoSuchAlgorithmException | CertificateException | SignatureException | InvalidKeyException ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateSignature(String[] signatureCertificates, byte[] signature, byte[] content) {
        return this.validateSignature(signatureCertificates, DigitalSignatureAlgorithmEnum.SHA2_384_WITH_ECDSA, signature, content);
    }
}
