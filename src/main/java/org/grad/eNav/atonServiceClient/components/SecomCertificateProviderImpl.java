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
import org.grad.secom.core.base.DigitalSignatureCertificate;
import org.grad.secom.core.base.SecomCertificateProvider;
import org.grad.secom.core.exceptions.SecomInvalidCertificateException;
import org.grad.secom.core.utils.KeyStoreUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
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
     * The X.509 Trust-Store.
     */
    @Value("${gla.rad.aton-service-client.secom.keyStore:keystore.jks}")
    String keyStore;

    /**
     * The X.509 Trust-Store Password.
     */
    @Value("${gla.rad.aton-service-client.secom.keyStorePassword:password}")
    String keyStorePassword;

    /**
     * The X.509 Trust-Store Type.
     */
    @Value("${gla.rad.aton-service-client.secom.keyStoreType:JKS}")
    String keyStoreType;

    /**
     * The X.509 Trust-Store.
     */
    @Value("${gla.rad.aton-service-client.secom.trustStore:truststore.jks}")
    String trustStore;

    /**
     * The X.509 Trust-Store Password.
     */
    @Value("${gla.rad.aton-service-client.secom.trustStorePassword:password}")
    String trustStorePassword;

    /**
     * The X.509 Trust-Store Type.
     */
    @Value("${gla.rad.aton-service-client.secom.trustStoreType:JKS}")
    String trustStoreType;

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
            keyStore = KeyStoreUtils.getKeyStore(this.keyStore, this.keyStorePassword, this.keyStoreType);
            certificate = (X509Certificate) keyStore.getCertificate(this.certificateAlias);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException ex) {
            throw new RuntimeException(ex);
        }

        // Get the root CA certificate from the trust store
        final KeyStore trustStore;
        final X509Certificate rootCertificate;
        try {
            trustStore = KeyStoreUtils.getKeyStore(this.trustStore, this.trustStorePassword, this.trustStoreType);
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

    @Override
    public KeyStore getTrustStore() {
        try {
            return KeyStoreUtils.getKeyStore(this.trustStore, this.trustStorePassword, this.trustStoreType);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException ex) {
            return null;
        }
    }

    /**
     * Returns a list of trusted certificates for the signature validation.
     * This is only required for SECOM consumers so the default operation does
     * not return any certificates. In this case however we need to load the
     * whole trust-store.
     *
     * @return the list of trusted certificates for SECOM
     */
    @Override
    public X509Certificate[] getTrustedCertificates() {
        // Get the provided truststore
        final KeyStore trustStore;
        try {
            trustStore = KeyStoreUtils.getKeyStore(this.trustStore, this.trustStorePassword, this.trustStoreType);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException ex) {
            throw new SecomInvalidCertificateException(ex.getMessage());
        }

        // Now load the PKIX parameters
        final PKIXParameters params;
        try {
            params = new PKIXParameters(trustStore);
        } catch (KeyStoreException | InvalidAlgorithmParameterException ex) {
            throw new SecomInvalidCertificateException(ex.getMessage());
        }

        // Access and return all the available certificates
        return params.getTrustAnchors().stream()
                .map(TrustAnchor::getTrustedCert)
                .toList()
                .toArray(X509Certificate[]::new);
    }

}
