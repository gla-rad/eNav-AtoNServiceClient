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
import org.grad.secomv2.core.models.SearchServiceUpdateObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The basic MSR Client Update Service Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic MSR UpdateService interface and generates the
 * data to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Lawrence Hughes (lawrence.hughes@gla-rad.org)
 */

@PactConsumerTest
@PactTestFor(providerName = "MsrV2Service")
public class BasicMsrV2UpdateServiceTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * A static UUID for tests
     */
    private static final String TEST_INSTANCE_UUID = "12345678-1234-1234-1234-1234567890ab";

    private static final String INVALID_UUID = "12345678-1234-1234-1234-1234567890abc";

    /**
     * MSR Update Service Pact with valid Instance ID
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact updateServicePact(PactBuilder builder) {
        return builder
                .given("Test MSR UpdateService Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid update service request, with a known InstanceId",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/updateService/" + TEST_INSTANCE_UUID)
                                        .method("PUT")
                                        .body(MsrPactDslDefinitions.updateSearchServiceRequestDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(MsrPactDslDefinitions.searchResponseDsl))
                )
                .toPact();
    }

    /**
     * MSR Update Service Pact with an invalid transaction ID
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV2Service", consumer="MsrV2ServiceClient")
    public V4Pact updateServiceInvalidIdPact(PactBuilder builder) {
        return builder
                .given("Test MSR UpdateService Interface")
                .expectsToReceiveHttpInteraction(
                        "An invalid/unknown InstanceID request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/updateService/" + INVALID_UUID)
                                        .method("PUT")
                                        .body(MsrPactDslDefinitions.updateSearchServiceRequestDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(404)
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
     * Test the client can send perform a service update request
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "updateServicePact")
    void testUpdateService(MockServer mockServer) throws IOException {

        SearchServiceUpdateObject searchServiceUpdateObject = new SearchServiceUpdateObject();
        String[] certificates = new String[1];
        certificates[0] = "ZGlnaXRhbFNpZ25hdHVyZQ==";
        searchServiceUpdateObject.setCertificates(certificates);
        searchServiceUpdateObject.setVersion("1.0");
        searchServiceUpdateObject.setEndpointUri("https://example.com");
        searchServiceUpdateObject.setApiDoc("https://example.com");
        searchServiceUpdateObject.setStatusEndpoint("https://example.com");

        // And perform the SearchService request
        Response httpResponse = Request.put(mockServer.getUrl() + "/v2/updateService/" + TEST_INSTANCE_UUID)
                .bodyString(this.objectMapper.writeValueAsString(searchServiceUpdateObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, httpResponse.returnResponse().getCode());
    }

    /**
     * Test the client receives an error if the InstanceId is invalid
     *
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "updateServiceInvalidIdPact")
    void testUpdateServiceWithInvalidId(MockServer mockServer) throws IOException {
        SearchServiceUpdateObject searchServiceUpdateObject = new SearchServiceUpdateObject();
        String[] certificates = new String[1];
        certificates[0] = "ZGlnaXRhbFNpZ25hdHVyZQ==";
        searchServiceUpdateObject.setCertificates(certificates);
        searchServiceUpdateObject.setVersion("1.0");
        searchServiceUpdateObject.setEndpointUri("https://example.com");
        searchServiceUpdateObject.setApiDoc("https://example.com");
        searchServiceUpdateObject.setStatusEndpoint("https://example.com");

        // And perform the SearchService request
        Response httpResponse = Request.put(mockServer.getUrl() + "/v2/updateService/" + INVALID_UUID)
                .bodyString(this.objectMapper.writeValueAsString(searchServiceUpdateObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(404, httpResponse.returnResponse().getCode());
    }

}
