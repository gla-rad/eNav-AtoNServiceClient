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

package org.grad.eNav.atonServiceClient.pacts.msrV1;

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
import org.grad.secom.core.models.SearchFilterObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The basic MSR Client Search Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic MSR Search Service interface and generates the data to be published to
 * the pacts-broker. This can be done through a separate maven goal, so that
 * it doesn't conflict with the development of the service.
 *
 * @author Lawrence Hughes (lawrence.hughes@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "MsrV1Service")
public class BasicMsrV1SearchServiceTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * SECOM Capability Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="MsrV1Service", consumer="MsrV1ServiceClient")
    public V4Pact createSearchResponsePact(PactBuilder builder) {
        return builder
                .given("Test MSR Search Service Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid search service request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/searchService")
                                        .method("POST")
                                        .body(MsrPactDslDefinitions.searchRequestObjectWithBlankQueryDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(MsrPactDslDefinitions.searchResponseDsl))
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
    @PactTestFor(pactMethods = "createSearchResponsePact")
    void testSearchService(MockServer mockServer) throws IOException {

        // Create a search filter object
        SearchFilterObject searchFilterObject = new SearchFilterObject();

        // And perform the SearchService request
        Response httpResponse = Request.post(mockServer.getUrl() + "/v1/searchService")
                .bodyString(this.objectMapper.writeValueAsString(searchFilterObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, httpResponse.returnResponse().getCode());
    }

}
