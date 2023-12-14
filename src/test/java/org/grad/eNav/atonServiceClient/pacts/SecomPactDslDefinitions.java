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

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;

import java.time.Instant;
import java.util.TimeZone;

import static org.grad.secom.core.base.SecomConstants.SECOM_DATE_TIME_FORMAT;

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
    static final DslPart capabilityDsl = new PactDslJsonBody()
            .array("capability")
                .object()
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
                    .stringType("serviceVersion", "0.0.1")
                .closeObject()
            .closeArray();

    /**
     * SECOM GetSummary Pact Body
     */
     static final DslPart getSummaryDsl = new PactDslJsonBody()
            .array("summaryObject")
                .object()
                    .stringMatcher("dataReference",  "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "7f000101-8ad6-1ee7-818a-d7332b920002")
                    .booleanType("dataProtection",  true)
                    .booleanType("dataCompression", true)
                    .integerMatching("containerType", "[0|1|2]", 1)
                    .stringMatcher("dataProductType", "S\\d{3}", "S125")
                    .stringType("info_identifier", "test")
                    .stringType("info_name", "test")
                    .stringType("info_status", "present")
                    .datetime("info_lastModifiedDate", SECOM_DATE_TIME_FORMAT + "XX", Instant.now(), TimeZone.getDefault())
                    .stringType("info_productVersion", "0.0.1")
                    .integerType("info_size", 12345)
                .closeObject()
            .closeArray()
            .asBody()
            .object("pagination")
                .integerType("totalItems", 1)
                .integerType("maxItemsPerPage", 1000)
            .closeObject()
            .asBody()
            .stringType("responseText", "");

    /**
     * SECOM Get Pact Body
     */
    static final DslPart getDsl = new PactDslJsonBody()
            .array("dataResponseObject")
                .object()
                    .stringMatcher("data",  "^[-A-Za-z0-9+/]*={0,3}$", "ZXhhbXBsZW9mYmFzZTY0ZW5jb2RlZGRhdGE=")
                    .object("exchangeMetadata", new PactDslJsonBody()
                            .booleanType("dataProtection",  true)
                            .stringType("protectionScheme", "SECOM")
                            .stringMatcher("digitalSignatureReference", "^(dsa|ecdsa|cvc_ecdsa)$", "dsa")
                            .object("digitalSignatureValue", new PactDslJsonBody()
                                    .stringMatcher("publicRootCertificateThumbprint", "^[-A-Za-z0-9+/]*={0,3}$", "cHVibGljUm9vdENlcnRpZmljYXRlVGh1bWJwcmludA===")
                                    .stringMatcher("publicCertificate", "^[-A-Za-z0-9+/]*={0,3}$", "cHVibGljQ2VydGlmaWNhdGU=")
                                    .stringMatcher("digitalSignature", "^[-A-Za-z0-9+/]*={0,3}$", "ZGlnaXRhbFNpZ25hdHVyZQ=="))
                            .booleanType("compressionFlag", false))
                    .integerMatching("ackRequest", "^[0123]$", 0)
                .closeObject()
            .closeArray()
            .asBody()
            .object("pagination")
                .integerType("totalItems", 1)
                .integerType("maxItemsPerPage", 100)
            .closeObject()
            .asBody()
            .stringType("responseText");


}
