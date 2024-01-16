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

package org.grad.eNav.atonServiceClient.pacts.utils;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import org.grad.secom.core.models.CapabilityResponseObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * The type Pact test helper test.
 */

public class PactTestHelperTest {

    /**
     * The Response body.
     */
    DslPart capabilityResponseBody;

    /**
     * Common setup for all the tests.
     */
    @Before
    public void setUp() {
        final LocalDateTime created = LocalDateTime.of(2019,12,31,12,0);
        capabilityResponseBody = LambdaDsl.newJsonBody(body ->
                body.arrayContaining("capability",
                        capability -> capability.object(capabilityObj -> capabilityObj
                                .numberValue("containerType", 0)
                                .stringValue("dataProductType", "S125")
                                .stringValue("productSchemaUrl", "https://rnavlab.gla-rad.org/enav/aton-service/xsd/S125.xsd")
                                .object("implementedInterfaces", implementedInterfacesObj -> implementedInterfacesObj
                                        .booleanType("upload", false)
                                        .booleanType("uploadLink", false)
                                        .booleanType("get", false)
                                        .booleanType("getSummary", false)
                                        .booleanType("getByLink", false)
                                        .booleanType("subscription", false)
                                        .booleanType("access", false)
                                        .booleanType("encryptionKey", false)
                                )
                                .stringValue("serviceVersion", "0.0.1")
                        )
                )
        ).build();
    }

    /**
     * Test pact dto mapping.
     *
     * @throws IOException the IO exception that occurred
     */
    @Test
    public void testPactDtoMapping() throws IOException {
        PactTestHelper.testPactDtoMapping(capabilityResponseBody,  CapabilityResponseObject.class);
    }
}
