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
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Secom Consumer Contact Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts and
 * generates then data to be publised to the pacts-broker. This can be done
 * through a separate maven goal, so that it doesn't conflict with the
 * development of the service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomS125Service")
class SecomS125ServiceClientTest {

    /**
     * SECOM Capability Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createCapabilityPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Capability success")
                .expectsToReceiveHttpInteraction(
                        "SecomS125ServiceConsumer test Get Capability interaction with success",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/capability")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.capabilityDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetSummaryPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary success")
                .expectsToReceiveHttpInteraction(
                        "SecomS125ServiceConsumer test Get Summary interaction with success",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.getSummaryDsl))
                )
                .toPact();
    }

    /**
     * Define a map of query parameters for the createGetSummaryPactWithParams
     * pacts. This can also be reused in the testing.
     */
    final Map<String, String> createGetSummaryPactWithParamsQueryMap = Map.of(
            "containerType" ,"0",
            "dataProductType" ,"S125",
            "productVersion" ,"0.0.1",
            "geometry" ,"POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90))",
            "unlocode" ,"GBHRW",
            "validFrom" ,"20200101T100000",
            "validTo" ,"20200101T1235959",
            "page" ,"0",
            "pageSize" ,"10"
    );

    /**
     * SECOM Get Summary With Parameters Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetSummaryPactWithParams(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary success")
                .expectsToReceiveHttpInteraction(
                        "SecomS125ServiceConsumer test Get Summary with query parameters interaction with success\"",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.createGetSummaryPactWithParamsQueryMap))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.getSummaryDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Get success")
                .expectsToReceiveHttpInteraction(
                        "SecomS125ServiceConsumer test Get interaction with success",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.getDsl))
                )
                .toPact();
    }

    /**
     * Test that the client can request the SECOM capabilities of the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createCapabilityPact")
    void testCapability(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(mockServer.getUrl() + "/v1/capability");
        assertEquals(httpResponse.getStatusCode(), 200);
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPact")
    void testGetSummary(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(mockServer.getUrl() + "/v1/object/summary");
        assertEquals(httpResponse.getStatusCode(), 200);
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
    void testGetSummaryWithParams(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object/summary",
                Maps.transformValues(this.createGetSummaryPactWithParamsQueryMap, v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 200);
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPact")
    void testGet(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(mockServer.getUrl() + "/v1/object");
        assertEquals(httpResponse.getStatusCode(), 200);
    }

}
