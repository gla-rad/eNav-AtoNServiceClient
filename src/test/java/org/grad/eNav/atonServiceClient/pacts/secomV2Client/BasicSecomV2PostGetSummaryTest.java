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

package org.grad.eNav.atonServiceClient.pacts.secomV2Client;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.grad.secomv2.core.models.EnvelopeGetSummaryFilterObject;
import org.grad.secomv2.core.models.GetSummaryFilterObject;
import org.grad.secomv2.core.models.enums.ContainerTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The basic SECOM Client Consumer Get Summary Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for the
 * basic SECOM Client Consumer Get Summary interface and generates the data
 * to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomV2Service")
public class BasicSecomV2PostGetSummaryTest {

    static final String POST_GET_SUMMARY_PATH = "/v2/object/search/summary";

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * SECOM Get Summary Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetSummaryPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary POST Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid get summary request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_SUMMARY_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getSummaryFilterObjectDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomV2PactDslDefinitions.getSummaryResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary Pact with parameters.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetSummaryPactWithParams(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary POST Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid get summary request with query",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_SUMMARY_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getSummaryFilterObjectWithCriteriaDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomV2PactDslDefinitions.getSummaryResponseDsl))
                )
                .toPact();
    }


    /**
     * SECOM Get Summary With Parameters of Badly Formatted Container Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetSummaryPactWithParamsContainerTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary POST Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but a badly formatted containerType",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_SUMMARY_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getSummaryFilterObjectInvalidContainerTypeDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomV2PactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters of invalid page number.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact createGetSummaryPactWithParamsPageNumberInvalid(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary POST Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but an invalid page number",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path(POST_GET_SUMMARY_PATH)
                                        .method("POST")
                                        .body(SecomV2PactDslDefinitions.getSummaryFilterObjectWithInvalidPageNumberDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomV2PactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        this.objectMapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl
                        .withContentInclusion(JsonInclude.Include.NON_NULL)
                        .withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPact")
    void testGetSummary(MockServer mockServer) throws IOException, URISyntaxException {
        // Create the envelope get summary filter object
        EnvelopeGetSummaryFilterObject envelopeGetSummaryFilterObject = new EnvelopeGetSummaryFilterObject();
        envelopeGetSummaryFilterObject.setEnvelopeSignatureCertificate(new String[]{"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetSummaryFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetSummaryFilterObject.setEnvelopeSignatureTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        // Create the get summary filter object
        GetSummaryFilterObject getSummaryFilterObject = new GetSummaryFilterObject();
        getSummaryFilterObject.setEnvelope(envelopeGetSummaryFilterObject);
        getSummaryFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_SUMMARY_PATH)
                .bodyString(this.objectMapper.writeValueAsString(getSummaryFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters and generate the pacts to be uploaded to the
     * pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParams")
    void testGetSummaryWithParams(MockServer mockServer) throws IOException, URISyntaxException {
        // Create the envelope get summary filter object
        EnvelopeGetSummaryFilterObject envelopeGetSummaryFilterObject = new EnvelopeGetSummaryFilterObject();
        envelopeGetSummaryFilterObject.setContainerType(ContainerTypeEnum.S100_DataSet);
        envelopeGetSummaryFilterObject.setUnlocode("GBHRW");
        envelopeGetSummaryFilterObject.setEnvelopeSignatureCertificate(new String[]{"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetSummaryFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetSummaryFilterObject.setEnvelopeSignatureTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        // Create the get summary filter object
        GetSummaryFilterObject getSummaryFilterObject = new GetSummaryFilterObject();
        getSummaryFilterObject.setEnvelope(envelopeGetSummaryFilterObject);
        getSummaryFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_SUMMARY_PATH)
                .bodyString(this.objectMapper.writeValueAsString(getSummaryFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters, including a badly formatted container type
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParamsContainerTypeBadFormat")
    void testGetSummaryWithParamsContainerTypeBadFormat(MockServer mockServer) throws IOException, URISyntaxException {
        // Create the envelope get summary filter object
        EnvelopeGetSummaryFilterObject envelopeGetSummaryFilterObject = new EnvelopeGetSummaryFilterObject();
        envelopeGetSummaryFilterObject.setContainerType(ContainerTypeEnum.S100_DataSet);
        envelopeGetSummaryFilterObject.setEnvelopeSignatureCertificate(new String[]{"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetSummaryFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetSummaryFilterObject.setEnvelopeSignatureTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        // Create the get summary filter object
        GetSummaryFilterObject getSummaryFilterObject = new GetSummaryFilterObject();
        getSummaryFilterObject.setEnvelope(envelopeGetSummaryFilterObject);
        getSummaryFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        String jsonBody = this.objectMapper.writeValueAsString(getSummaryFilterObject).replace("\"containerType\":0", "\"containerType\":9");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_SUMMARY_PATH)
                .bodyString(jsonBody, ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters, including an invalid page number
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParamsPageNumberInvalid")
    void testGetSummaryWithParamsPageNumberInvalid(MockServer mockServer) throws IOException, URISyntaxException {
        // Create the envelope get summary filter object
        EnvelopeGetSummaryFilterObject envelopeGetSummaryFilterObject = new EnvelopeGetSummaryFilterObject();
        envelopeGetSummaryFilterObject.setPage(0);
        envelopeGetSummaryFilterObject.setEnvelopeSignatureCertificate(new String[]{"ZGlnaXRhbFNpZ25hdHVyZQ=="});
        envelopeGetSummaryFilterObject.setEnvelopeRootCertificateThumbprint("714fead3e2e4f0a01051bc4e26c30a306c456ef1");
        envelopeGetSummaryFilterObject.setEnvelopeSignatureTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        // Create the get summary filter object
        GetSummaryFilterObject getSummaryFilterObject = new GetSummaryFilterObject();
        getSummaryFilterObject.setEnvelope(envelopeGetSummaryFilterObject);
        getSummaryFilterObject.setEnvelopeSignature("ZGlnaXRhbFNpZ25hdHVyZQ==");

        // Perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + POST_GET_SUMMARY_PATH)
                .bodyString(this.objectMapper.writeValueAsString(getSummaryFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }


}
