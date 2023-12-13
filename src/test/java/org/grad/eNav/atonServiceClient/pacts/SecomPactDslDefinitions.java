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

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

/**
 * The SECOM Pact DSL Definitions class.
 * <p/>
 * This class is a utility that contains all the consumer contract DSL
 * definitions to be used for publishing to the pacts broker.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SecomPactDslDefinitions {

    /**
     * SECOM Capability Pact Body
     */
    static final PactDslJsonBody capabilityDsl = new PactDslJsonBody()
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

}
