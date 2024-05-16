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

package org.grad.eNav.atonServiceClient.pacts.s125ServiceClient;

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
     * SECOM Capability Response Pact Body
     */
    static final DslPart capabilityResponseDsl = new PactDslJsonBody()
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
            .closeArray()
            .asBody();

    /**
     * SECOM GetSummary Response Pact Body
     */
     static final DslPart getSummaryResponseDsl = new PactDslJsonBody()
            .array("summaryObject")
                .object()
                    .stringMatcher("dataReference",  "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "7f000101-8ad6-1ee7-818a-d7332b920002")
                    .booleanType("dataProtection",  true)
                    .booleanType("dataCompression", true)
                    .integerMatching("containerType", "[0|1|2]", 1)
                    .stringValue("dataProductType", "S125")
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
     * SECOM GetSummary Response Error Pact Body
     */
    static final DslPart getSummaryResponseErrorDsl = new PactDslJsonBody()
            .stringType("responseText", "");

    /**
     * SECOM Get Pact Body
     */
    static final DslPart getResponseDsl = new PactDslJsonBody()
            .array("dataResponseObject")
                .object()
                    .stringMatcher("data",  "^[-A-Za-z0-9+/]*={0,3}$", "ZXhhbXBsZW9mYmFzZTY0ZW5jb2RlZGRhdGE=")
                    .object("exchangeMetadata", new PactDslJsonBody()
                            .booleanType("dataProtection",  true)
                            .stringType("protectionScheme", "SECOM")
                            .stringMatcher("digitalSignatureReference", "^(dsa|ecdsa-256-sha2-256|ecdsa-256-sha3-256|ecdsa-384-sha2|ecdsa-384-sha3|cvc_ecdsa)$", "dsa")
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

    /**
     * SECOM Get Response Error Pact Body
     */
    static final DslPart getResponseErrorDsl = new PactDslJsonBody()
            .stringType("responseText", "");

    /**
     * SECOM Acknowledgement Request Pact Body
     */
    static final DslPart acknowledgementRequestDsl = new PactDslJsonBody()
            .object("envelope")
                .datetime("createdAt", SECOM_DATE_TIME_FORMAT + "XX", Instant.now(), TimeZone.getDefault())
                .stringMatcher("envelopeCertificate", "^[-A-Za-z0-9+/]*={0,3}$", "ZW52ZWxvcGVDZXJ0aWZpY2F0ZQ==")
                .stringMatcher("envelopeRootCertificateThumbprint",  "^[-A-Za-z0-9+/]*$", "714fead3e2e4f0a01051bc4e26c30a306c456ef1")
                .stringMatcher("transactionIdentifier",  "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "3fa85f64-5717-4562-b3fc-2c963f66afa6")
                .integerMatching("ackType", "[1|2|3]", 1)
                .integerMatching("nackType", "[0|1|2|3|4]", 0)
                .datetime("envelopeSignatureTime", SECOM_DATE_TIME_FORMAT + "XX", Instant.now(), TimeZone.getDefault())
            .closeObject()
            .asBody()
            .stringMatcher("digitalSignature",  "^[-A-Za-z0-9+/]*={0,3}$", "ZGlnaXRhbFNpZ25hdHVyZQ==");

    /**
     * SECOM Acknowledgement Request Pact Body Without Transaction Identifier
     */
    static final DslPart acknowledgementRequestWithoutTransactionIdentifierDsl = new PactDslJsonBody()
            .object("envelope")
            .datetime("createdAt", SECOM_DATE_TIME_FORMAT + "XX", Instant.now(), TimeZone.getDefault())
            .stringMatcher("envelopeCertificate", "^[-A-Za-z0-9+/]*={0,3}$", "ZW52ZWxvcGVDZXJ0aWZpY2F0ZQ==")
            .stringMatcher("envelopeRootCertificateThumbprint",  "^[-A-Za-z0-9+/]*$", "714fead3e2e4f0a01051bc4e26c30a306c456ef1")
            .nullValue("transactionIdentifier")
            .integerMatching("ackType", "[1|2|3]", 1)
            .integerMatching("nackType", "[0|1|2|3|4]", 0)
            .datetime("envelopeSignatureTime", SECOM_DATE_TIME_FORMAT + "XX", Instant.now(), TimeZone.getDefault())
            .closeObject()
            .asBody()
            .stringMatcher("digitalSignature",  "^[-A-Za-z0-9+/]*={0,3}$", "ZGlnaXRhbFNpZ25hdHVyZQ==");

    /**
     * SECOM Acknowledgement Response Pact Body
     */
    static final DslPart acknowledgementResponseDsl = new PactDslJsonBody()
            .nullValue("SECOM_ResponseCode")
            .stringType("message",  "Acknowledgement message.");

    /**
     * SECOM Acknowledgement Response Error Pact Body
     */
    static final DslPart acknowledgementResponseErrorDsl = new PactDslJsonBody()
            .integerMatching("SECOM_ResponseCode", "[0|1|2|3]", 0)
            .stringType("message",  "Acknowledgement message.");

    /**
     * SECOM Subscription Request Pact Body
     */
    static final DslPart subscriptionRequestDsl = new PactDslJsonBody()
            .integerMatching("containerType", "[0|1|2]", 1)
            .stringValue("dataProductType", "S125")
            .stringMatcher("dataReference",  "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "7f000101-8ad6-1ee7-818a-d7332b920002")
            .stringType("productVersion", "0.0.1")
            .stringMatcher("geometry", "^([A-Z]+\\s*\\(\\(?\\s*(-?\\d+(\\.\\d+)?)\\s+-?\\d+(\\.\\d+)?(?:\\s+-?\\d+(\\.\\d+)?)?\\s*(,\\s*(-?\\d+(\\.\\d+)?)\\s+-?\\d+(\\.\\d+)?(?:\\s+-?\\d+(\\.\\d+)?)?\\s*)*\\)\\)?\\s*)+$", "POLYGON ((-180 -90, -180 90, 180 90, 180 -90, -180 -90))")
            .stringMatcher("unlocode",  "[A-Z]{5}", "GBHRW")
            .datetime("subscriptionPeriodStart", SECOM_DATE_TIME_FORMAT + "XX", Instant.now(), TimeZone.getDefault())
            .datetime("subscriptionPeriodEnd", SECOM_DATE_TIME_FORMAT + "XX", Instant.now(), TimeZone.getDefault());

    /**
     * SECOM Subscription Response Pact Body
     */
    static final DslPart subscriptionResponseDsl = new PactDslJsonBody()
            .stringType("message", "Subscription successfully created")
            .stringMatcher("subscriptionIdentifier",  "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "3fa85f64-5717-4562-b3fc-2c963f66afa6");

    /**
     * SECOM Subscription Response Error Pact Body
     */
    static final DslPart subscriptionResponseErrorDsl = new PactDslJsonBody()
            .stringType("message", "Bad Request");

    /**
     * SECOM Remove Subscription Request Pact Body
     */
    static final DslPart removeSubscriptionRequestDsl = new PactDslJsonBody()
            .stringMatcher("subscriptionIdentifier",  "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "7f000101-8ad6-1ee7-818a-d7332b920002");

    /**
     * SECOM Remove Subscription Response Pact Body
     */
    static final DslPart removeSubscriptionResponseDsl = new PactDslJsonBody()
            .stringType("message", "Subscription successfully removed");

    /**
     * SECOM Remove Subscription Response Error Pact Body
     */
    static final DslPart removeSubscriptionResponseErrorDsl = new PactDslJsonBody()
            .stringType("message", "Bad Request");

    /**
     * SECOM Remove Subscription Response Error Pact Body
     */
    static final DslPart removeSubscriptionResponseNotFoundDsl = new PactDslJsonBody()
            .stringType("message", "Subscriber identifier not found");
}
