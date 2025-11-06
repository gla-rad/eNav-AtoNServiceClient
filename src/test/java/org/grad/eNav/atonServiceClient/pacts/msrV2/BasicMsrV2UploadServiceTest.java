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
import org.grad.secomv2.core.models.ResponseSearchObject;
import org.grad.secomv2.core.models.SearchObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

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
public class BasicMsrV2UploadServiceTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * A static UUID for tests
     */
    private static final String TEST_UUID = "12345678-1234-1234-1234-1234567890ab";

    /**
     * MSR Global Search Upload Results Pact with valid transaction ID
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact uploadValidResultsServicePact(PactBuilder builder) {
        return builder
                .given("Test MSR Upload Service Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid upload results request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/uploadResults/" + TEST_UUID)
                                        .method("POST")
                                        .body(MsrPactDslDefinitions.globalSearchUploadDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200))
                )
                .toPact();
    }

    /**
     * MSR Global Search Upload Results Pact with valid transaction ID
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact uploadInvalidSourceMSRPact(PactBuilder builder) {
        return builder
                .given("Test MSR Upload Service Interface")
                .expectsToReceiveHttpInteraction(
                        "A request with an invalid source MSR MRN",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/uploadResults/" + TEST_UUID)
                                        .method("POST")
                                        .body(MsrPactDslDefinitions.globalSearchInvalidMSRUploadDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400))
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
    @PactTestFor(pactMethods = "uploadValidResultsServicePact")
    void testUploadValidResults(MockServer mockServer) throws IOException {
        ResponseSearchObject responseSearchObject = new ResponseSearchObject();
        ArrayList<SearchObjectResult> searchResults = new ArrayList<>();

        SearchObjectResult searchResult = new SearchObjectResult();
        searchResult.setTransactionId(UUID.fromString(TEST_UUID));
        searchResult.setInstanceId("urn:mrn:mcp:service:mcc:grad:instance:aton-service");
        searchResult.setVersion("0.0.4");
        searchResult.setName("GRAD AtoN Service");
        searchResult.setStatus("PROVISIONAL");
        searchResult.setDescription("GRAD Test AtoN Service");
        searchResult.setOrganizationId("urn:mrn:mcp:org:mcc:grad");
        searchResult.setEndpointUri("https://rnavlab.gla-rad.org/enav/aton-service/api/secom");
        searchResult.setApiDoc("https://rnavlab.gla-rad.org/enav/aton-service/apiDoc");
        String[] coverageArea = new String[1];
        coverageArea[0] = "POLYGON ((0.65 51.42, 0.65 52.26, 2.68 52.26, 2.68 51.42, 0.65 51.42))";
        searchResult.setCoverageArea(coverageArea);
        searchResult.setSourceMSR("urn:mrn:mcp:msr:test-msr");

        searchResults.add(searchResult);
        responseSearchObject.setSearchServiceResult(searchResults);

        // And perform the Upload Results request
        Response httpResponse = Request.post(mockServer.getUrl() + "/v2/uploadResults/" + TEST_UUID)
                .bodyString(objectMapper.writeValueAsString(responseSearchObject), org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, httpResponse.returnResponse().getCode());
    }

    @Test
    @PactTestFor(pactMethods = "uploadInvalidSourceMSRPact")
    void testUploadInvalidSourceMSR(MockServer mockServer) throws IOException {
        ResponseSearchObject responseSearchObject = new ResponseSearchObject();
        ArrayList<SearchObjectResult> searchResults = new ArrayList<>();

        SearchObjectResult searchResult = new SearchObjectResult();
        searchResult.setTransactionId(UUID.fromString(TEST_UUID));
        searchResult.setInstanceId("urn:mrn:mcp:service:mcc:grad:instance:aton-service");
        searchResult.setVersion("0.0.4");
        searchResult.setName("GRAD AtoN Service");
        searchResult.setStatus("PROVISIONAL");
        searchResult.setDescription("GRAD Test AtoN Service");
        searchResult.setOrganizationId("urn:mrn:mcp:org:mcc:grad");
        searchResult.setEndpointUri("https://rnavlab.gla-rad.org/enav/aton-service/api/secom");
        searchResult.setApiDoc("https://rnavlab.gla-rad.org/enav/aton-service/apiDoc");
        String[] coverageArea = new String[1];
        coverageArea[0] = "POLYGON ((0.65 51.42, 0.65 52.26, 2.68 52.26, 2.68 51.42, 0.65 51.42))";
        searchResult.setCoverageArea(coverageArea);
        searchResult.setSourceMSR("urn:mrn:mcp:wrong:test-msr");

        searchResults.add(searchResult);
        responseSearchObject.setSearchServiceResult(searchResults);

        // And perform the Upload Results request
        Response httpResponse = Request.post(mockServer.getUrl() + "/v2/uploadResults/" + TEST_UUID)
                .bodyString(objectMapper.writeValueAsString(responseSearchObject), org.apache.hc.core5.http.ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, httpResponse.returnResponse().getCode());
    }

}
