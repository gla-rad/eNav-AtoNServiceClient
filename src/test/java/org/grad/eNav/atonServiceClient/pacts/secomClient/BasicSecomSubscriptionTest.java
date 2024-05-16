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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.grad.secom.core.models.SubscriptionRequestObject;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The basic SECOM Client Consumer Subscription Interface Contract Test Class.
 * <p/>
 * This class provides the definition of the consumer-driver contracts for a
 * basic SECOM client Consumer Subscription interface and generates the data
 * to be published to the pacts-broker. This can be done through a separate
 * maven goal, so that it doesn't conflict with the development of the service.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@PactConsumerTest
@PactTestFor(providerName = "SecomService")
public class BasicSecomSubscriptionTest {

    /**
     * The test object mapper.
     */
    ObjectMapper objectMapper;

    /**
     * SECOM Subscription Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPact(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A valid subscription request",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body(SecomPactDslDefinitions.subscriptionRequestDsl))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.subscriptionResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Bad Body Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithBadBody(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formed body",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"field1\":\"bad-field\", \"field2\":\"bad-field\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(SecomPactDslDefinitions.subscriptionResponseDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Badly Formatted Container Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithContainerTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formatted containerType",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"containerType\":\"invalid\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.subscriptionResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Badly Formatted Data Product Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithDataProductTypeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formatted containerType",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"dataProductType\":\"invalid\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.subscriptionResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Badly Formatted Data Reference Type Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithDataReferenceBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formatted dataReference",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"dataReference\":\"invalid\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.subscriptionResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Badly Formatted Geometry Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithGeometryBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formatted geometry",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"geometry\":\"invalid\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.subscriptionResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Badly Formatted UnLoCode Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithUnLoCodeBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formatted unlocode",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"unlocode\":\"invalid\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.subscriptionResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Badly Formatted Subscription Period Start Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithSubscriptionPeriodStartBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formatted subscriptionPeriodStart",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"subscriptionPeriodStart\":\"invalid\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.subscriptionResponseErrorDsl))
                )
                .toPact();
    }

    /**
     * SECOM Subscription With Badly Formatted Subscription Period End Pact.
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomService", consumer="SecomServiceClient")
    public V4Pact subscriptionPactWithSubscriptionPeriodEndBadFormat(PactBuilder builder) {
        return builder
                .given("Test SECOM Subscription Interface")
                .expectsToReceiveHttpInteraction(
                        "A subscription request with a badly formatted subscriptionPeriodEnd",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/subscription")
                                        .method("POST")
                                        .body("{\"subscriptionPeriodEnd\":\"invalid\"}"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(400)
                                        .body(SecomPactDslDefinitions.subscriptionResponseErrorDsl))
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
     * Test that the client can request the SECOM subscription of the server
     * and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPact")
    void testSubscription(MockServer mockServer) throws IOException {
        // Create an subscription request object
        SubscriptionRequestObject subscriptionRequestObject = new SubscriptionRequestObject();
        subscriptionRequestObject.setContainerType(ContainerTypeEnum.S100_DataSet);
        subscriptionRequestObject.setDataReference(UUID.randomUUID());
        subscriptionRequestObject.setProductVersion("0.0.1");
        subscriptionRequestObject.setGeometry("POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90))");
        subscriptionRequestObject.setUnlocode("GBHRW");
        subscriptionRequestObject.setSubscriptionPeriodStart(LocalDateTime.now());
        subscriptionRequestObject.setSubscriptionPeriodEnd(LocalDateTime.now());

        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString(this.objectMapper.writeValueAsString(subscriptionRequestObject), ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a badly formed subscription request
     * on the server and generate the pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithBadBody")
    void createSubscriptionPactWithBadBody(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"field1\":\"bad-field\", \"field2\":\"bad-field\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(200, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a subscription request with a badly
     * formatted containerType field on the server and generate the pacts to be
     * uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithContainerTypeBadFormat")
    void createSubscriptionPactWithContainerTypeBadFormat(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"containerType\":\"invalid\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a subscription request with a badly
     * formatted dataProductType field on the server and generate the pacts to be
     * uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithDataProductTypeBadFormat")
    void createSubscriptionPactWithDataProductTypeBadFormat(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"dataProductType\":\"invalid\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a subscription request with a badly
     * formatted dataReference field on the server and generate the pacts to be
     * uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithDataReferenceBadFormat")
    void createSubscriptionPactWithDataReferenceBadFormat(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"dataReference\":\"invalid\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a subscription request with a badly
     * formatted productVersion field on the server and generate the pacts to be
     * uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithGeometryBadFormat")
    void createSubscriptionPactWithGeometryBadFormat(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"geometry\":\"invalid\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a subscription request with a badly
     * formatted unlocode field on the server and generate the pacts to be
     * uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithUnLoCodeBadFormat")
    void createSubscriptionPactWithUnLoCodeBadFormat(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"unlocode\":\"invalid\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a subscription request with a badly
     * formatted subscriptionPeriodStart field on the server and generate the
     * pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithSubscriptionPeriodStartBadFormat")
    void createSubscriptionPactWithSubscriptionPeriodStartBadFormat(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"subscriptionPeriodStart\":\"invalid\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

    /**
     * Test that the client cannot perform a subscription request with a badly
     * formatted subscriptionPeriodStart field on the server and generate the
     * pacts to be uploaded to the pacts broker.
     * @param mockServer the mocked server
     */
    @Test
    @PactTestFor(pactMethods = "subscriptionPactWithSubscriptionPeriodEndBadFormat")
    void createSubscriptionPactWithSubscriptionPeriodEndBadFormat(MockServer mockServer) throws IOException {
        // And perform the SECOM request
        Response response = Request.post(mockServer.getUrl() + "/v1/subscription")
                .bodyString("{\"subscriptionPeriodEnd\":\"invalid\"}", ContentType.APPLICATION_JSON)
                .execute();
        assertEquals(400, response.returnResponse().getCode());
    }

}
