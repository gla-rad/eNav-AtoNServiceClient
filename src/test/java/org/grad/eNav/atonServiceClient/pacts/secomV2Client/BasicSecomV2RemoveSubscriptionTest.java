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

package org.grad.eNav.atonServiceClient.pacts.secomV2Client;

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
import org.grad.secom.core.models.RemoveSubscriptionObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The basic SECOM Client Consumer Remove Subscription Interface Contract
 * Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic SECOM Client Consumer Remove Subscription interface and generates
 * the data to be published to the pacts-broker. This can be done through a
 * separate maven goal, so that it doesn't conflict with the development of the
 * service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomV2Service")
public class BasicSecomV2RemoveSubscriptionTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * SECOM Remove Subscription Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact removeSubscriptionPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Remove Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid remove subscription request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/subscription")
                                        .method("DELETE")
                                        .body(SecomV2PactDslDefinitions.removeSubscriptionRequestDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomV2PactDslDefinitions.removeSubscriptionResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Remove Subscription With Bad Body Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact removeSubscriptionPactWithBadBody(PactBuilder builder) {
        return builder
                .given("Test SECOM Remove Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A remove subscription request with a badly formed body",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/subscription")
                                        .method("DELETE")
                                        .body("{\"field1\":\"bad-field\", \"field2\":\"bad-field\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomV2PactDslDefinitions.subscriptionResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Remove Subscription With Bad Body Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomV2Service", consumer="SecomV2ServiceClient")
    public V4Pact removeSubscriptionPactNotFound(PactBuilder builder) {
        return builder
                .given("Test SECOM Remove Subscription Interface without subscriptions")
                .expectsToReceiveHttpInteraction(
                        "A remove subscription request for a subscription identifier that does not exist",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v2/subscription")
                                        .method("DELETE")
                                        .body(SecomV2PactDslDefinitions.removeSubscriptionRequestDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(404)
                                        .body(SecomV2PactDslDefinitions.removeSubscriptionResponseNotFoundDsl))
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
     * Test that the client can perform a SECOM remove subscription request on
     * the server and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "removeSubscriptionPact")
    void testRemoveSubscription(MockServer mockServer) throws IOException {
        // Create an subscription request object
        RemoveSubscriptionObject removeSubscriptionObject = new RemoveSubscriptionObject();
        removeSubscriptionObject.setSubscriptionIdentifier(UUID.randomUUID());

        // And perform the SECOM request
        Response httpResponse = Request.delete(mockServer.getUrl() + "/v2/subscription")
                .bodyString(this.objectMapper.writeValueAsString(removeSubscriptionObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(httpResponse.returnResponse().getCode(), 200);
    }

    /**
     * Test that the client cannot perform a badly formed remove subscription
     * request on the server and generate the pacts to be uploaded to the pacts
     * broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "removeSubscriptionPactWithBadBody")
    void testRemoveSubscriptionPactWithBadBody(MockServer mockServer) throws IOException {
        // Create an subscription request object
        RemoveSubscriptionObject removeSubscriptionObject = new RemoveSubscriptionObject();
        removeSubscriptionObject.setSubscriptionIdentifier(UUID.randomUUID());

        // And perform the SECOM request
        Response response = Request.delete(mockServer.getUrl() + "/v2/subscription")
                .bodyString("{\"field1\":\"bad-field\", \"field2\":\"bad-field\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a remove subscription request on the
     * server for an invalid subscription identifier and generate the pacts to
     * be uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "removeSubscriptionPactNotFound")
    void testRemoveSubscriptionPactNotFound(MockServer mockServer) throws IOException {
        // Create an subscription request object
        RemoveSubscriptionObject removeSubscriptionObject = new RemoveSubscriptionObject();
        removeSubscriptionObject.setSubscriptionIdentifier(UUID.randomUUID());

        // And perform the SECOM request
        Response response = Request.delete(mockServer.getUrl() + "/v2/subscription")
                .bodyString(this.objectMapper.writeValueAsString(removeSubscriptionObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(404, response.returnResponse().getCode());
    }

}
