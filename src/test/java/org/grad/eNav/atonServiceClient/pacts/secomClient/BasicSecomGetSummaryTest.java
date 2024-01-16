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

package org.grad.eNav.atonServiceClient.pacts.secomClient;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The basic SECOM Client Consumer Get Summary Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for the
 * basic SECOM Client Consumer Get Summary interface and generates the data
 * to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomService")
public class BasicSecomGetSummaryTest {

    /**
     * Define a map of query parameters for the createGetSummaryPactWithParams
     * pacts. This can also be reused in the testing.
     */
    final Map<String, String> queryParamsMap = Map.of(
            "containerType", "0",
            "dataProductType", "S125",
            "productVersion", "0.0.1",
            "geometry", "POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90))",
            "unlocode", "GBHRW",
            "validFrom", "20200101T000000",
            "validTo", "20200101T235959",
            "page", "0",
            "pageSize", "10"
    );

    /**
     * SECOM Get Summary Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid get summary request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.getSummaryResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPactWithParams(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid get summary request with query parameters",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.queryParamsMap))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.getSummaryResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters of Badly Formatted Container Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPactWithParamsContainerTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but a badly formatted containerType",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "containerType", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters of Badly Formatted Data Product Type
     * Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPactWithParamsDataProductTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but a badly formatted dataProductType",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "dataProductType", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters of Badly Formatted Geometry Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPactWithParamsGeometryBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but a badly formatted geometry",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "geometry", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters of Badly Formatted UnLoCode Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPactWithParamsUnLoCodeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but a badly formatted UnLoCode",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "unlocode", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters of Badly Formatted validFrom Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPactWithParamsValidFromBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but a badly formatted validFrom",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "validFrom", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get Summary With Parameters of Badly Formatted validTo Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createGetSummaryPactWithParamsValidToBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get Summary Interface")
                .expectsToReceiveHttpInteraction(
                        "A get summary request with query parameters but a badly validTo",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object/summary")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "validTo", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getSummaryResponseErrorDsl))
                )
                .toPact();
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
        // Perform the SECOM request
        Response response = Request.get(
                new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                        .build())
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
        // Perform the SECOM request
        Response response = Request.get(
                new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                        .addParameters(this.mapToNameValueParams(this.queryParamsMap))
                        .build())
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
        // Update the query params
        final Map<String, String> queryParams = this.updateMapValue(this.queryParamsMap, "containerType", "invalid");
        // Perform the SECOM request
        Response response = Request.get(
                        new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                                .addParameters(this.mapToNameValueParams(queryParams))
                                .build())
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters, including a badly formatted data product type
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParamsDataProductTypeBadFormat")
    void testGetSummaryWithParamsDataProductTypeBadFormat(MockServer mockServer) throws IOException, URISyntaxException {
        // Update the query params
        final Map<String, String> queryParams = this.updateMapValue(this.queryParamsMap, "dataProductType", "invalid");
        // Perform the SECOM request
        Response response = Request.get(
                        new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                                .addParameters(this.mapToNameValueParams(queryParams))
                                .build())
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters, including a badly formatted geometry
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParamsGeometryBadFormat")
    void testGetSummaryWithParamsGeometryBadFormat(MockServer mockServer) throws IOException, URISyntaxException {
        // Update the query params
        final Map<String, String> queryParams = this.updateMapValue(this.queryParamsMap, "geometry", "invalid");
        // Perform the SECOM request
        Response response = Request.get(
                        new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                                .addParameters(this.mapToNameValueParams(queryParams))
                                .build())
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters, including a badly formatted UnLoCode
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParamsUnLoCodeBadFormat")
    void testGetSummaryWithParamsUnLoCodeBadFormat(MockServer mockServer) throws IOException, URISyntaxException {
        // Update the query params
        final Map<String, String> queryParams = this.updateMapValue(this.queryParamsMap, "unlocode", "invalid");
        // Perform the SECOM request
        Response response = Request.get(
                        new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                                .addParameters(this.mapToNameValueParams(queryParams))
                                .build())
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters, including a badly formatted validFrom
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParamsValidFromBadFormat")
    void testGetSummaryWithParamsValidFromBadFormat(MockServer mockServer) throws IOException, URISyntaxException {
        // Update the query params
        final Map<String, String> queryParams = this.updateMapValue(this.queryParamsMap, "validFrom", "invalid");
        // Perform the SECOM request
        Response response = Request.get(
                        new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                                .addParameters(this.mapToNameValueParams(queryParams))
                                .build())
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client can request the SECOM Get Summary of the server
     * alongside query parameters, including a badly formatted validTo
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetSummaryPactWithParamsValidToBadFormat")
    void testGetSummaryWithParamsValidToBadFormat(MockServer mockServer) throws IOException, URISyntaxException {
        // Update the query params
        final Map<String, String> queryParams = this.updateMapValue(this.queryParamsMap, "validTo", "invalid");
        // Perform the SECOM request
        Response response = Request.get(
                        new URIBuilder(mockServer.getUrl() + "/v1/object/summary")
                                .addParameters(this.mapToNameValueParams(queryParams))
                                .build())
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * A helper function that will copy the provided map and change one of its
     * value, without messing the original map.
     *
     * @param map the map to be updated
     * @param key the key to update
     * @param value the new value to be used
     * @return a new map with the updated value in the respective key
     */
    private Map<String, String> updateMapValue(Map<String, String> map, String key, String value) {
        final Map<String, String>  newMap = new HashMap<>(map);
        newMap.put(key, value);
        return newMap;
    }

    /**
     * A helper function that transforms all the entries of the provided string
     * map to name-value pairs.
     *
     * @param map the map with the header string values
     * @return the generated name-value pairs
     */
    private List<NameValuePair> mapToNameValueParams(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .map(NameValuePair.class::cast)
                .toList();
    }

}
