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
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The basic SECOM Client Consumer Ping Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic SECOM Client Consumer Ping interface and generates the data
 * to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomService")
public class BasicSecomPingTest {

    /**
     * SECOM Ping Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact createPingPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Ping Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid ping request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/ping")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.pingResponseDsl))
                )
                .toPact();
    }

    /**
     * Test that the client can request the SECOM ping of the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     * @throws IOException the IO exception that occurred
     */
    @Test
    @PactTestFor(pactMethods = "createPingPact")
    void testPing(MockServer mockServer) throws IOException {
        // Perform the SECOM request
        Response response = Request.get(mockServer.getUrl() + "/v1/ping")
                .execute();
        assertEquals(200, response.returnResponse().getCode());
    }

}
