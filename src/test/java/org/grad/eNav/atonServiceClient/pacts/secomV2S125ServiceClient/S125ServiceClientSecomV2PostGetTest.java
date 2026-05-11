/*
 * Copyright (c) 2026 GLA Research and Development Directorate
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

package org.grad.eNav.atonServiceClient.pacts.secomV2S125ServiceClient;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.grad.secomv2.core.models.EnvelopeGetFilterObject;
import org.grad.secomv2.core.models.GetFilterObject;
import org.grad.secomv2.core.models.enums.ContainerTypeEnum;
import org.grad.secomv2.core.models.enums.SECOM_DataProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The basic SECOM client Consumer Get Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic SECOM client Consumer Get interface and generates the data to be
 * published to the pacts-broker. This can be done through a separate maven
 * goal, so that it doesn't conflict with the development of the service.
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomV2Service")
class S125ServiceClientSecomV2PostGetTest {

    static final String POST_GET_PATH = "/v2/object/search";

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * SECOM Get Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetPact(PactBuilder builder) {
        return builder
                .given("Test SECOM POST Get Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid get request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getFilterObjectDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomV2PactDslDefinitions.getResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetPactWithParams(PactBuilder builder) {
        return builder
                .given("Test SECOM POST Get Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid get request with query parameters",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getFilterObjectWithCriteriaDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomV2PactDslDefinitions.getResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of Badly Formatted Container Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetPactWithParamsContainerTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM POST Get Interface")
                .expectsToReceiveHttpInteraction(
                        "A get request with query parameters but a badly formatted containerType",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getFilterObjectWithInvalidContainerTypeDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomV2PactDslDefinitions.getResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of invalid page number.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetPactWithParamsPageNumberInvalid(PactBuilder builder) {
        return builder
                .given("Test SECOM POST Get Interface")
                .expectsToReceiveHttpInteraction(
                        "A get request with query parameters but an invalid page number",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getFilterObjectWithInvalidPageNumberDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomV2PactDslDefinitions.getResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPact")
    void testGet(MockServer mockServer) throws IOException, URISyntaxException {

        // Create the envelope
        EnvelopeGetFilterObject envelopeGetFilterObject = new EnvelopeGetFilterObject();
        envelopeGetFilterObject.setEnvelopeSignatureCertificate(new String[] {"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetFilterObject.setEnvelopeSignatureTime(Instant.now());

        // Create the filter object
        GetFilterObject getFilterObject = new GetFilterObject();
        getFilterObject.setEnvelope(envelopeGetFilterObject);
        getFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_PATH)
                .bodyString(this.objectMapper.writeValueAsString(getFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, response.returnResponse().getCode());

    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters and generate the pacts to be uploaded to the
     * pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParams")
    void testGetWithParams(MockServer mockServer) throws IOException, URISyntaxException {
        // Create the envelope
        EnvelopeGetFilterObject envelopeGetFilterObject = new EnvelopeGetFilterObject();
        envelopeGetFilterObject.setContainerType(ContainerTypeEnum.S100_DataSet);
        envelopeGetFilterObject.setUnlocode("GBHRW");
        envelopeGetFilterObject.setDataProductType(SECOM_DataProductType.S125);
        envelopeGetFilterObject.setEnvelopeSignatureCertificate(new String[] {"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetFilterObject.setEnvelopeSignatureTime(Instant.now());

        // Create the filter object
        GetFilterObject getFilterObject = new GetFilterObject();
        getFilterObject.setEnvelope(envelopeGetFilterObject);
        getFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_PATH)
                .bodyString(this.objectMapper.writeValueAsString(getFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters, including a badly formatted container type
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParamsContainerTypeBadFormat")
    void testGetWithParamsContainerTypeBadFormat(MockServer mockServer) throws IOException, URISyntaxException {
        // Create the envelope
        EnvelopeGetFilterObject envelopeGetFilterObject = new EnvelopeGetFilterObject();
        envelopeGetFilterObject.setContainerType(ContainerTypeEnum.S100_DataSet);
        envelopeGetFilterObject.setDataProductType(SECOM_DataProductType.S125);
        envelopeGetFilterObject.setEnvelopeSignatureCertificate(new String[] {"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetFilterObject.setEnvelopeSignatureTime(Instant.now());

        // Create the filter object
        GetFilterObject getFilterObject = new GetFilterObject();
        getFilterObject.setEnvelope(envelopeGetFilterObject);
        getFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        String jsonBody = this.objectMapper.writeValueAsString(getFilterObject);
        jsonBody = jsonBody.replace("\"containerType\":0", "\"containerType\":9");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_PATH)
                .bodyString(jsonBody, ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters, including an invalid page number
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParamsPageNumberInvalid")
    void testGetWithParamsPageNumberInvalid(MockServer mockServer) throws IOException, URISyntaxException {
        // Create the envelope
        EnvelopeGetFilterObject envelopeGetFilterObject = new EnvelopeGetFilterObject();
        envelopeGetFilterObject.setDataProductType(SECOM_DataProductType.S125);
        envelopeGetFilterObject.setPage(0);
        envelopeGetFilterObject.setEnvelopeSignatureCertificate(new String[] {"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetFilterObject.setEnvelopeSignatureTime(Instant.now());

        // Create the filter object
        GetFilterObject getFilterObject = new GetFilterObject();
        getFilterObject.setEnvelope(envelopeGetFilterObject);
        getFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_PATH)
                .bodyString(this.objectMapper.writeValueAsString(getFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

}
