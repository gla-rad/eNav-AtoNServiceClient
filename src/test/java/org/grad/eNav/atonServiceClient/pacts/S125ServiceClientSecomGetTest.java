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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The S125 Aton client Consumer SECOM Get Interface Contact Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for the
 * S125 AtoN Service Client SECOM Get interface and generates then data
 * to be publised to the pacts-broker. This can be done through a separate maven
 * goal, so that it doesn't conflict with the development of the service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomS125Service")
class S125ServiceClientSecomGetTest {

    /**
     * Define a map of query parameters for the createGetPactWithParams
     * pacts. This can also be reused in the testing.
     */
    final Map<String, String> queryParamsMap = Map.of(
            "containerType" ,"0",
            "dataProductType" ,"S125",
            "productVersion" ,"0.0.1",
            "geometry" ,"POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90))",
            "unlocode" ,"GBHRW",
            "validFrom" ,"20200101T000000",
            "validTo" ,"20200101T235959",
            "page" ,"0",
            "pageSize" ,"10"
    );

    /**
     * SECOM Get Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get interaction with success",
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
     * SECOM Get With Parameters Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPactWithParams(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get with query parameters interaction with success\"",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET")
                                        .queryParameters(this.queryParamsMap))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.getDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of Badly Formatted Container Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPactWithParamsContainerTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get with query parameters badly formatted containertype interaction with failure",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "containerType", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of Badly Formatted Data Product Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPactWithParamsDataProductTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get with query parameters badly formatted dataProductType interaction with failure",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "dataProductType", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of Badly Formatted Geometry Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPactWithParamsGeometryBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get with query parameters badly formatted geometry interaction with failure",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "geometry", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of Badly Formatted UnLoCode Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPactWithParamsUnLoCodeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get with query parameters badly formatted UnLoCode interaction with failure",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "unlocode", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of Badly Formatted validFrom Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPactWithParamsValidFromBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get with query parameters badly formatted validFrom interaction with failure",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "validFrom", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Get With Parameters of Badly Formatted validTo Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createGetPactWithParamsValidToBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Get")
                .expectsToReceiveHttpInteraction(
                        "Test Get with query parameters badly formatted validTo interaction with failure",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/object")
                                        .method("GET")
                                        .queryParameters(this.updateMapValue(this.queryParamsMap, "validTo", "invalid")))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.getErrorDsl))
                )
                .toPact();
    }

    /**
     * Test that the client can request the SECOM Get of the server
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

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters and generate the pacts to be uploaded to the
     * pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParams")
    void testGetWithParams(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object",
                Maps.transformValues(
                        this.queryParamsMap,
                        v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 200);
        assertTrue(httpResponse.getHasBody());
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
    void testGetWithParamsContainerTypeBadFormat(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object",
                Maps.transformValues(
                        this.updateMapValue(this.queryParamsMap, "containerType", "invalid"),
                        v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 400);
        assertTrue(httpResponse.getHasBody());
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters, including a badly formatted data product type
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParamsDataProductTypeBadFormat")
    void testGetWithParamsDataProductTypeBadFormat(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object",
                Maps.transformValues(
                        this.updateMapValue(this.queryParamsMap, "dataProductType", "invalid"),
                        v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 400);
        assertTrue(httpResponse.getHasBody());
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters, including a badly formatted geometry
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParamsGeometryBadFormat")
    void testGetWithParamsGeometryBadFormat(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object",
                Maps.transformValues(
                        this.updateMapValue(this.queryParamsMap, "geometry", "invalid"),
                        v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 400);
        assertTrue(httpResponse.getHasBody());
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters, including a badly formatted UnLoCode
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParamsUnLoCodeBadFormat")
    void testGetWithParamsUnLoCodeBadFormat(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object",
                Maps.transformValues(
                        this.updateMapValue(this.queryParamsMap, "unlocode", "invalid"),
                        v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 400);
        assertTrue(httpResponse.getHasBody());
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters, including a badly formatted validFrom
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParamsValidFromBadFormat")
    void testGetWithParamsValidFromBadFormat(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object",
                Maps.transformValues(
                        this.updateMapValue(this.queryParamsMap, "validFrom", "invalid"),
                        v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 400);
        assertTrue(httpResponse.getHasBody());
    }

    /**
     * Test that the client can request the SECOM Get of the server
     * alongside query parameters, including a badly formatted validTo
     * query parameter, and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createGetPactWithParamsValidToBadFormat")
    void testGetWithParamsValidToBadFormat(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(
                mockServer.getUrl() + "/v1/object",
                Maps.transformValues(
                        this.updateMapValue(this.queryParamsMap, "validTo", "invalid"),
                        v -> URLEncoder.encode(v, StandardCharsets.UTF_8)));
        assertEquals(httpResponse.getStatusCode(), 400);
        assertTrue(httpResponse.getHasBody());
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

}
