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
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.support.Response;
import au.com.dius.pact.core.support.SimpleHttp;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@PactConsumerTest
@PactTestFor(providerName = "SecomS125Service")
class SecomConsumerContractTest {

    /**
     * SECOM Capability Pact Body
     */
    final PactDslJsonBody capabilityDsl = new PactDslJsonBody()
            .array("capability").object()
                            .numberValue("containerType", 0)
                            .stringValue("dataProductType", "S125")
                            .stringType("productSchemaUrl", "https://rnavlab.gla-rad.org/enav/aton-service/xsd/S125.xsd")
                            .object("implementedInterfaces", new PactDslJsonBody()
                                    .booleanType("upload",  true)
                                    .booleanType("uploadLink",  true)
                                    .booleanType("get", true)
                                    .booleanType("getSummary", true)
                                    .booleanType("getByLink", true)
                                    .booleanType("subscription", true)
                                    .booleanType("access", true)
                                    .booleanType("encryptionKey", true)
                            )
                            .stringType("serviceVersion", "0.0.1");

    /**
     * SECOM Capability Pact
     * @param builder The Pact Builder
     */
    @Pact(provider="SecomS125Service", consumer="SecomS125ServiceClient")
    public V4Pact createCapabilityPact(PactBuilder builder) {
        return builder
                .given("Test SECOM GET capability success")
                .expectsToReceiveHttpInteraction(
                        "SecomS125ServiceConsumer test GET capability interaction with success",
                        httpBuilder -> httpBuilder
                                .withRequest(requestBuilder -> requestBuilder
                                        .path("/v1/capability")
                                        .method("GET"))
                                .willRespondWith(responseBuilder -> responseBuilder
                                        .status(200)
                                        .body(this.capabilityDsl))
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
    void testCapability(MockServer mockServer) throws IOException {
        SimpleHttp http = new SimpleHttp(mockServer.getUrl());
        Response httpResponse = http.get(mockServer.getUrl() + "/v1/capability");
        assertEquals(httpResponse.getStatusCode(), 200);
    }

}
