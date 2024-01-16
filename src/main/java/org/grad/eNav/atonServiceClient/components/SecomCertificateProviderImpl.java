/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonServiceClient.components;

import lombok.extern.slf4j.Slf4j;
import org.grad.secom.core.base.DigitalSignatureCertificate;
import org.grad.secom.core.base.SecomCertificateProvider;
import org.grad.secom.core.utils.KeyStoreUtils;
import org.grad.secom.springboot3.components.SecomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * The SECOM Certificate Provider Implementation.
 *
 * In the current e-Navigation Service Architecture, it's the cKeeper
 * microservice that is responsible for storing and providing the certificates
 * to be used for signing and verifying the messages.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Component
@Slf4j
public class SecomCertificateProviderImpl implements SecomCertificateProvider {

    /**
     * The Application Name.
     */
    @Value("${spring.application.name:aton-service}")
    String appName;

    /**
     * The X.509 Certificate Alias.
     */
    @Value("${gla.rad.aton-service-client.secom.certificateAlias:certificate}")
    String certificateAlias;

    /**
     * The X.509 Root Certificate Alias.
     */
    @Value("${gla.rad.aton-service-client.secom.rootCertificateAlias:rootCertificate}")
    String rootCertificateAlias;

    /**
     * The SECOM Configuration properties.
     */
    @Autowired
    SecomConfigProperties secomConfigProperties;

    /**
     * This function overrides the interface definition to link the SECOM
     * certificate provision with the cKeeper operation. Each service can
     * request its most recent certificate, alongside some additional
     * information such as its ID, public key and root certificate thumbprint.
     *
     * @return the certificate information provided by cKeeper
     */
    @Override
    public DigitalSignatureCertificate getDigitalSignatureCertificate() {
        // Get the certificate from the trust store
        final KeyStore keyStore;
        final X509Certificate certificate;
        try {
            keyStore = KeyStoreUtils.getKeyStore(this.secomConfigProperties.getKeystore(), this.secomConfigProperties.getKeystorePassword(), this.secomConfigProperties.getKeystoreType());
            certificate = (X509Certificate) keyStore.getCertificate(this.certificateAlias);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException ex) {
            throw new RuntimeException(ex);
        }

        // Get the root CA certificate from the trust store
        final KeyStore trustStore;
        final X509Certificate rootCertificate;
        try {
            trustStore = KeyStoreUtils.getKeyStore(this.secomConfigProperties.getTruststore(), this.secomConfigProperties.getTruststorePassword(), this.secomConfigProperties.getTruststoreType());
            rootCertificate = (X509Certificate) trustStore.getCertificate(this.rootCertificateAlias);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException ex) {
            throw new RuntimeException(ex);
        }


        // Build the SECOM digital certificate object
        final DigitalSignatureCertificate digitalSignatureCertificate = new DigitalSignatureCertificate();
        digitalSignatureCertificate.setCertificateAlias(this.appName);
        digitalSignatureCertificate.setCertificate(certificate);
        digitalSignatureCertificate.setPublicKey(certificate.getPublicKey());
        digitalSignatureCertificate.setRootCertificate(rootCertificate);

        // Return the output
        return digitalSignatureCertificate;
    }

}
