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
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.grad.secomv2.core.models.SearchFilterObject;
import org.grad.secomv2.core.models.SearchParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The basic MSR Client Search Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic MSR Search Service interface and generates the
 * data to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Lawrence Hughes (lawrence.hughes@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "MsrV2Service")
public class BasicMsrV2SearchServiceTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * Parameter map for testing the GET
     */
    final Map<String, String> queryParamsMap = Map.of(
            "geometry", "POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90))",
            "designId", "urn:mrn:",
            "mmsi", "123456789",
            "imo", "1234567",
            "instanceId", "urn:mrn:mcp:service:mcc:grad:instance:aton-service-client",
            "status", "RELEASED",
            "localOnly", "true"
    );

    /**
     * SECOM Search Service with a blank, valid query using POST
     * Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact createSearchResponsePostPact(PactBuilder builder) {
        return builder
                .given("Test MSR Search Service POST Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid search service request using POST",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/searchService")
                                        .method("POST")
                                        .body(MsrPactDslDefinitions.searchRequestObjectWithBlankQueryDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(MsrPactDslDefinitions.searchResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Search Service with an invalid status field in the query, using POST
     * Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact createInvalidSearchResponsePostPact(PactBuilder builder) {
        return builder
                .given("Test MSR Search Service POST Interface")
                .expectsToReceiveHttpInteraction(
                        "A search service request using POST with invalid body",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/searchService")
                                        .method("POST")
                                        .body(MsrPactDslDefinitions.searchRequestObjectWithInvalidStatusDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(MsrPactDslDefinitions.errorResponseObject))
                )
                .toPact();
    }

    /**
     * SECOM Search Service with a blank, valid query using GET
     * Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact createSearchResponseGetPact(PactBuilder builder) {
        return builder
                .given("Test MSR Search Service GET Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid search service request using GET",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/searchService")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(MsrPactDslDefinitions.searchResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Search Service with all required parameters, using GET
     * Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact createSearchResponseGetWithParamsPact(PactBuilder builder) {
        return builder
                .given("Test MSR Search Service GET Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid search service request using GET with mandatory query parameters",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/searchService")
                                        .method("GET")
                                        .queryParameters(queryParamsMap))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(MsrPactDslDefinitions.searchResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Search Service with an invalid status parameter, using GET
     * Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact createInvalidSearchResponseGetWithParamsPact(PactBuilder builder) {
        return builder
                .given("Test MSR Search Service GET Interface")
                .expectsToReceiveHttpInteraction(
                        "An invalid search service request using GET with bad query parameters",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/searchService")
                                        .method("GET")
                                        .queryParameters(updateMapValue(queryParamsMap, "status", "INVALID-STATUS")))
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
     * Test the client can send perform a search request using POST
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createSearchResponsePostPact")
    void testSearchServicePost(MockServer mockServer) throws IOException {

        // Create a search filter object
        SearchFilterObject searchFilterObject = new SearchFilterObject();

        // And perform the SearchService request
        Response httpResponse = Request.post(mockServer.getUrl() + "/v2/searchService")
                .bodyString(this.objectMapper.writeValueAsString(searchFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, httpResponse.returnResponse().getCode());
    }

    /**
     * Test the client gets a 400 error when the search parameters are incorrect, using POST
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createInvalidSearchResponsePostPact")
    void testSearchServicePostWithInvalidParams(MockServer mockServer) throws IOException, URISyntaxException {
        // Create a search filter object

        SearchFilterObject searchFilterObject = new SearchFilterObject();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setStatus("INVALID-STATUS");
        searchFilterObject.setQuery(searchParameters);

        // And perform the SearchService request
        Response httpResponse = Request.post(mockServer.getUrl() + "/v2/searchService")
                .bodyString(this.objectMapper.writeValueAsString(searchFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, httpResponse.returnResponse().getCode());
    }

    /**
     * Test the client can send perform a search request using GET
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createSearchResponseGetPact")
    void testSearchServiceGet(MockServer mockServer) throws IOException {

        // And perform the SearchService request
        Response httpResponse = Request.get(mockServer.getUrl() + "/v2/searchService")
                .execute();
        assertEquals(200, httpResponse.returnResponse().getCode());
    }

    /**
     * Test the client can send perform a search request with all required parameters, using GET
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createSearchResponseGetWithParamsPact")
    void testSearchServiceGetWithParams(MockServer mockServer) throws IOException, URISyntaxException {

        // And perform the SearchService request
        Response httpResponse = Request.get(
                new URIBuilder(mockServer.getUrl() + "/v2/searchService")
                        .addParameters(this.mapToNameValueParams(queryParamsMap))
                        .build())

                .execute();
        assertEquals(200, httpResponse.returnResponse().getCode());
    }

    /**
     * Test the client gets a 400 error when the search parameters are incorrect, using GET
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createInvalidSearchResponseGetWithParamsPact")
    void testSearchServiceGetWithInvalidParams(MockServer mockServer) throws IOException, URISyntaxException {

        // And perform the SearchService request
        Response httpResponse = Request.get(
                        new URIBuilder(mockServer.getUrl() + "/v2/searchService")
                                .addParameters(this.mapToNameValueParams(updateMapValue(queryParamsMap, "status", "INVALID-STATUS")))
                                .build())

                .execute();
        assertEquals(400, httpResponse.returnResponse().getCode());
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
