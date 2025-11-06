/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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

package org.grad.eNav.atonServiceClient.pacts.msrV1;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

/**
 * The MSR Pact DSL Definitions class.
 * <p/>
 * This class is a utility that contains all the consumer contract DSL
 * definitions to be used for publishing to the pacts broker.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class MsrPactDslDefinitions {


    /**
     * MSR Search Service Response test
     */
    static final DslPart searchResponseDsl = new PactDslJsonBody()
            .array("searchObjectResult")
                .object()
                    .stringMatcher("instanceId", "^(urn):(mrn):(mcp):(service):(mcc):.*$", "urn:mrn:mcp:service:mcc:grad:instance:aton-service-client")
                    .stringMatcher("version", "^(\\d+\\.\\d+)$", "0.1")
                    .stringType("name", "GRAD AtoN Service")
                    .stringMatcher("status", "^(PROVISIONAL|RELEASED|DEPRECATED|DELETED)$", "RELEASED")
                    .array("dataProductType")
                        .stringMatcher("^((S-?\\d\\d\\d?)|EPC|RTZ|ASM|OTHER)$", "S125")
                    .closeArray().asBody()
                    .stringMatcher("organizationId", "^(urn):(mrn):(mcp):.*$", "urn:mrn:mcp:grad:org:0000000000")
                    .stringMatcher("endpointUri", "^(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search")
                    .array("endpointType").closeArray()
                    .array("keywords").closeArray()
                    .array("unlocode").closeArray()
                    .array("implementsDesign")
                        .stringMatcher("^(urn):(mrn):.*$", "urn:mrn:mcp:service:mcc:grad:instance:aton-service-client")
                    .closeArray()
                    .array("coverageArea")
                        .stringType()
                    .closeArray()
                    .array("certificates")
                        .stringMatcher( "^[A-Za-z0-9+/]*={0,3}$", "cHVibGljQ2Vy+dG/lmaWNhdGU=")
                    .closeArray()
                .closeObject()
            .closeArray().asBody();


    /**
     * MSR Valid SearchRequestObject with empty query
     */
    static final DslPart searchRequestObjectWithBlankQueryDsl = new PactDslJsonBody()
            .nullValue("query")
            .nullValue("geometry")
            .nullValue("freetext")
            .asBody();

}
