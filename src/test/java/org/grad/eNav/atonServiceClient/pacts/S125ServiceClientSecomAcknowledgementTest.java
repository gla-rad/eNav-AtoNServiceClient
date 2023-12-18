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

package org.grad.eNav.atonServiceClient.pacts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.support.Response;
import au.com.dius.pact.core.support.SimpleHttp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.grad.secom.core.models.AcknowledgementObject;
import org.grad.secom.core.models.EnvelopeAckObject;
import org.grad.secom.core.models.enums.AckTypeEnum;
import org.grad.secom.core.models.enums.NackTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The S125 Aton client Consumer SECOM Acknowledgement Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for the
 * S125 AtoN Service Client SECOM Acknowledgement interface and generates the
 * data to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomS125Service")
public class S125ServiceClientSecomAcknowledgementTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * SECOM Capability Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createAcknowledgementPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Post Acknowledgement")
                .expectsToReceiveHttpInteraction(
                        "Test Post Acknowledgement interaction with success",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/acknowledgement")
                                        .method("POST")
                                        .headers("content-type", ContentType.APPLICATION_JSON.getMimeType())
                                        .body(SecomPactDslDefinitions.acknowledgementRequestDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.acknowledgementResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Capability Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createAcknowledgementPactWithBadBody(PactBuilder builder) {
        return builder
                .given("Test SECOM Post Acknowledgement")
                .expectsToReceiveHttpInteraction(
                        "Test Post Acknowledgement interaction for badly formed body with failure",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/acknowledgement")
                                        .method("POST")
                                        .headers("content-type", ContentType.APPLICATION_JSON.getMimeType())
                                        .body("{\"envelope\":\"bad-envelope\", \"digitalSignature\":\"bad-digital-signature\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.acknowledgementResponseErrorDsl.))
                )
                .toPact();
    }

    /**
     * SECOM Capability Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createAcknowledgementPactWithoutTransactionIdentifier(PactBuilder builder) {
        return builder
                    .given("Test SECOM Post Acknowledgement")
                .expectsToReceiveHttpInteraction(
                        "Test Post Acknowledgement interaction without transaction identifier",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/acknowledgement")
                                        .method("POST")
                                        .headers("content-type", ContentType.APPLICATION_JSON.getMimeType())
                                        .body(SecomPactDslDefinitions.acknowledgementRequestWithoutTransactionIdentifierDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.acknowledgementResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Test that the client can perform an acknowledgement update on the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createAcknowledgementPact")
    void testAcknowledgement(MockServer mockServer) throws IOException {
        // Create an acknowledgement object
        AcknowledgementObject acknowledgementObject = new AcknowledgementObject();
        EnvelopeAckObject envelopeAckObject = new EnvelopeAckObject();
        envelopeAckObject.setCreatedAt(LocalDateTime.now());
        envelopeAckObject.setEnvelopeSignatureCertificate("ZW52ZWxvcGVDZXJ0aWZpY2F0ZQ==");
        envelopeAckObject.setEnvelopeRootCertificateThumbprint("ZW52ZWxvcGVSb290Q2VydGlmaWNhdGVUaHVtYnByaW50=");
        envelopeAckObject.setTransactionIdentifier(UUID.randomUUID());
        envelopeAckObject.setAckType(AckTypeEnum.DELIVERED_ACK);
        envelopeAckObject.setNackType(NackTypeEnum.XML_SCHEMA_VALIDATION_ERROR);
        envelopeAckObject.setEnvelopeSignatureTime(LocalDateTime.now());
        acknowledgementObject.setEnvelope(envelopeAckObject);
        acknowledgementObject.setDigitalSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // And post it to the mock server
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.post(mockServer.getUrl() + "/v1/acknowledgement",
                this.objectMapper.writeValueAsString(acknowledgementObject),
                ContentType.APPLICATION_JSON.getMimeType());
        assertEquals(httpResponse.getStatusCode(), 200);
    }

    /**
     * Test that the client cannot perform a badly formed acknowledgement update
     * on the server and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createAcknowledgementPactWithBadBody")
    void testAcknowledgementWithBodBody(MockServer mockServer) throws IOException {
        // And post it to the mock server
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.post(mockServer.getUrl() + "/v1/acknowledgement",
                "{\"envelope\":\"bad-envelope\", \"digitalSignature\":\"bad-digital-signature\"}",
                ContentType.APPLICATION_JSON.getMimeType());
        assertEquals(httpResponse.getStatusCode(), 400);
    }

    /**
     * est that the client cannot perform an acknowledgement update on the
     * server if no transaction identifier is present and generate the pacts to
     * be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createAcknowledgementPactWithoutTransactionIdentifier")
    void testAcknowledgementWithoutTransactionIdentifier(MockServer mockServer) throws IOException {
        // Create an acknowledgement object
        AcknowledgementObject acknowledgementObject = new AcknowledgementObject();
        EnvelopeAckObject envelopeAckObject = new EnvelopeAckObject();
        envelopeAckObject.setCreatedAt(LocalDateTime.now());
        envelopeAckObject.setEnvelopeSignatureCertificate("ZW52ZWxvcGVDZXJ0aWZpY2F0ZQ==");
        envelopeAckObject.setEnvelopeRootCertificateThumbprint("ZW52ZWxvcGVSb290Q2VydGlmaWNhdGVUaHVtYnByaW50=");
        envelopeAckObject.setAckType(AckTypeEnum.DELIVERED_ACK);
        envelopeAckObject.setNackType(NackTypeEnum.XML_SCHEMA_VALIDATION_ERROR);
        envelopeAckObject.setEnvelopeSignatureTime(LocalDateTime.now());
        acknowledgementObject.setEnvelope(envelopeAckObject);
        acknowledgementObject.setDigitalSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // And post it to the mock server
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.post(mockServer.getUrl() + "/v1/acknowledgement",
                this.objectMapper.writeValueAsString(acknowledgementObject),
                ContentType.APPLICATION_JSON.getMimeType());
        assertEquals(httpResponse.getStatusCode(), 400);
    }

}