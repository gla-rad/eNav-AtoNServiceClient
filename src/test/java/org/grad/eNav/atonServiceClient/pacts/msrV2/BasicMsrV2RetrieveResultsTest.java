/*
 * Copyright (c) 2025 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grad.eNav.atonServiceClient.pacts.msrV2;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The basic MSR Client Retrieve Results Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic MSR Retrieve Results interface and generates the
 * data to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Lawrence Hughes (lawrence.hughes@gla-rad.org)
 */

@PactConsumerTest
@PactTestFor(providerName = "MsrV2Service")
public class BasicMsrV2RetrieveResultsTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * A static UUID for tests
     */
    private static final String TEST_UUID = "12345678-1234-1234-1234-1234567890ab";

    private static final String INVALID_UUID = "12345678-1234-1234-1234-1234567890abc";

    /**
     * MSR Retrieve Results Pact with valid transaction ID
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact retrieveResultsPact(PactBuilder builder) {
        return builder
                .given("Test MSR RetrieveResults Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid retrieve results response, with a known UUID",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/retrieveResults/" + TEST_UUID)
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(MsrPactDslDefinitions.searchResponseDsl))
                )
                .toPact();
    }

    /**
     * MSR Retrieve Results Pact with an invalid transaction ID
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact retrieveResultsInvalidUuidPact(PactBuilder builder) {
        return builder
                .given("Test MSR RetrieveResults Interface")
                .expectsToReceiveHttpInteraction(
                        "An invalid/unknown UUID request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/retrieveResults/" + INVALID_UUID)
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(404)
                                        .body(MsrPactDslDefinitions.errorResponseObject))
                )
                .toPact();
    }

    /**
     * MSR Search Service Pact without a transaction ID
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact retrieveResultsWithoutTransactionIDPact(PactBuilder builder) {
        return builder
                .given("Test MSR RetrieveResults Interface")
                .expectsToReceiveHttpInteraction(
                        "A retrieve results request without a transaction Id",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/retrieveResults")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(MsrPactDslDefinitions.errorResponseObject))
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
     * Test the client can send perform a search request
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "retrieveResultsPact")
    void testRetrieveResults(MockServer mockServer) throws IOException {

        // And perform the SearchService request
        Response httpResponse = Request.get(mockServer.getUrl() + "/v2/retrieveResults/" + TEST_UUID)
                .execute();
        assertEquals(200, httpResponse.returnResponse().getCode());
    }

    /**
     * Test the client receives an error if the request is incomplete
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "retrieveResultsWithoutTransactionIDPact")
    void testRetrieveResultsWithoutTransactionID(MockServer mockServer) throws IOException {

        // And perform the SearchService request
        Response httpResponse = Request.get(mockServer.getUrl() + "/v2/retrieveResults")
                .execute();
        assertEquals(400, httpResponse.returnResponse().getCode());
    }

    /**
     * Test the client receives an error if the request is incorrect
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "retrieveResultsInvalidUuidPact")
    void testRetrieveResultsWithInvalidTransactionID(MockServer mockServer) throws IOException {

        // And perform the SearchService request
        Response httpResponse = Request.get(mockServer.getUrl() + "/v2/retrieveResults/" + INVALID_UUID)
                .execute();
        assertEquals(404, httpResponse.returnResponse().getCode());
    }

}
