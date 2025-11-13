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

package org.grad.eNav.atonServiceClient.pacts.msrV2;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslRootValue;

import java.time.Instant;
import java.util.TimeZone;

import static org.grad.secomv2.core.base.SecomConstants.SECOM_DATE_TIME_FORMAT;

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
            .stringMatcher("transactionId", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "12345678-1234-1234-1234-1234567890ab")
            .array("searchServiceResult")
            .object()
            .stringMatcher("transactionIdentifier", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "12345678-1234-1234-1234-1234567890ab")
            .stringMatcher("instanceId", "^(urn):(mrn):(mcp):(service):(mcc):.*$", "urn:mrn:mcp:service:mcc:grad:instance:aton-service-client")
            .stringMatcher("version", "^(\\d+\\.\\d+\\.?\\d?+)$", "0.1")
            .stringType("name", "GRAD AtoN Service")
            .stringMatcher("status", "^(PROVISIONAL|RELEASED|DEPRECATED|DELETED)$", "RELEASED")
            .stringType("description","AtoN Service")
            .nullValue("dataProductType")
            .stringMatcher("organizationId", "^(urn):(mrn):(mcp):.*$", "urn:mrn:mcp:grad:org:0000000000")
            .stringMatcher("endpointUri", "^(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search")
            .nullValue("endpointType")
            .nullValue("keywords")
            .nullValue("unlocode")
            .object("geometry")
            .stringType("type", "Polygon")
            .array("coordinates")
            .closeArray()
            .closeObject()
            .asBody()
//            .nullValue("implementsDesign")
//            .stringMatcher("apiDoc", "^(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search/api-doc")
//            .array("coverageArea")
//            .stringType()
//            .closeArray()
//            .asBody()
            .nullValue("comment")
            .nullValue("instanceAsXml")
            .datetime("publishedAt", SECOM_DATE_TIME_FORMAT + "XXX", Instant.now(), TimeZone.getDefault())
            .datetime("lastUpdatedAt", SECOM_DATE_TIME_FORMAT + "XXX", Instant.now(), TimeZone.getDefault())
            .nullValue("imo")
            .nullValue("mmsi")
            .nullValue("certificates")
            .nullValue("sourceMSR")
//            .nullValue("unsupportedParams")
            .closeObject()
            .closeArray().asBody();

    /**
     * MSR Global Search Upload object
     */
    static final DslPart globalSearchUploadDsl = new PactDslJsonBody()
            .array("searchServiceResult")
            .object()
            .stringMatcher("transactionId", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "12345678-1234-1234-1234-1234567890ab")
            .stringMatcher("instanceId", "^(urn):(mrn):(mcp):(service):(mcc):.*$", "urn:mrn:mcp:service:mcc:grad:instance:aton-service-client")
            .stringMatcher("version", "^(\\d+\\.\\d+\\.?\\d?+)$", "0.1")
            .stringType("name", "GRAD AtoN Service")
            .stringMatcher("status", "^(PROVISIONAL|RELEASED|DEPRECATED|DELETED)$", "RELEASED")
            .stringType("description","AtoN Service")
            .nullValue("dataProductType")
            .stringMatcher("organizationId", "^(urn):(mrn):(mcp):.*$", "urn:mrn:mcp:grad:org:0000000000")
            .stringMatcher("endpointUri", "^(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search")
            .nullValue("endpointType")
            .nullValue("keywords")
            .nullValue("unlocode")
            .nullValue("implementsDesign")
            .stringMatcher("apiDoc", "^(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search/api-doc")
            .array("coverageArea")
            .stringType()
            .closeArray()
            .asBody()
            .nullValue("instanceAsXml")
            .nullValue("imo")
            .nullValue("mmsi")
            .nullValue("certificates")
            .stringMatcher("sourceMSR", "^(urn:mrn:mcp:msr:).*$", "urn:mrn:mcp:msr:test-mst-instance")
            .nullValue("unsupportedParams")
            .closeObject()
            .closeArray().asBody();

    /**
     * MSR Global Search Upload object
     */
    static final DslPart globalSearchInvalidMSRUploadDsl = new PactDslJsonBody()
            .array("searchServiceResult")
            .object()
            .stringMatcher("transactionId", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "12345678-1234-1234-1234-1234567890ab")
            .stringMatcher("instanceId", "^(urn):(mrn):(mcp):(service):(mcc):.*$", "urn:mrn:mcp:service:mcc:grad:instance:aton-service-client")
            .stringMatcher("version", "^(\\d+\\.\\d+\\.?\\d?+)$", "0.1")
            .stringType("name", "GRAD AtoN Service")
            .stringMatcher("status", "^(PROVISIONAL|RELEASED|DEPRECATED|DELETED)$", "RELEASED")
            .stringType("description","AtoN Service")
            .nullValue("dataProductType")
            .stringMatcher("organizationId", "^(urn):(mrn):(mcp):.*$", "urn:mrn:mcp:grad:org:0000000000")
            .stringMatcher("endpointUri", "^(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search")
            .nullValue("endpointType")
            .nullValue("keywords")
            .nullValue("unlocode")
            .nullValue("implementsDesign")
            .stringMatcher("apiDoc", "^(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search/api-doc")
            .array("coverageArea")
            .stringType()
            .closeArray()
            .asBody()
            .nullValue("instanceAsXml")
            .nullValue("imo")
            .nullValue("mmsi")
            .nullValue("certificates")
            .stringMatcher("sourceMSR", "^^(?!urn:mrn:mcp:msr:).*", "ura:mrn:mcp:msr:test-mst-instance")
            .nullValue("unsupportedParams")
            .closeObject()
            .closeArray().asBody();

    /**
     * MSR Update Service valid request
     */
    static final DslPart updateSearchServiceRequestDsl = new PactDslJsonBody()
            .array("certificates")
            .stringMatcher("^[-A-Za-z0-9+/]*={0,3}$", "ZGlnaXRhbFNpZ25hdHVyZQ==")
            .closeArray()
            .asBody()
            .stringMatcher("version", "^(\\d+\\.\\d+\\.?\\d?+)$", "0.1.1")
            .stringMatcher("endpointUri", "(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search")
            .stringMatcher("apiDoc", "(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search/api-doc")
            .stringMatcher("statusEndpoint", "(http|https)://.*$", "https://rnavlab.gla-rad.org/api/v1/search/api-doc")
            .asBody();


    /**
     * MSR Valid service update response object
     */
    static final DslPart validUpdateServiceResponseDsl = new PactDslJsonBody()
            .datetime("updatedAt", SECOM_DATE_TIME_FORMAT + "XXX", Instant.now(), TimeZone.getDefault());

    /**
     * MSR Valid SearchRequestObject with empty query
     */
    static final DslPart searchRequestObjectWithBlankQueryDsl = new PactDslJsonBody()
            .nullValue("query")
            .nullValue("geometry")
            .nullValue("includeXml")
            .nullValue("page")
            .nullValue("pageSize")
            .asBody();

    static final DslPart searchRequestObjectWithInvalidStatusDsl = new PactDslJsonBody()
            .object("query")
            .nullValue("name")
            .stringMatcher("status", "INVALID-STATUS")
            .nullValue("version")
            .nullValue("keywords")
            .nullValue("description")
            .nullValue("dataProductType")
            .booleanType("localOnly")
            .nullValue("specificationId")
            .nullValue("designId")
            .nullValue("instanceId")
            .nullValue("organizationId")
            .nullValue("mmsi")
            .nullValue("imo")
            .nullValue("serviceType")
            .nullValue("unlocode")
            .nullValue("endpointUri")
            .closeObject()
            .asBody()
            .nullValue("geometry")
            .nullValue("includeXml")
            .nullValue("page")
            .nullValue("pageSize")
            .asBody();

    static final DslPart errorResponseObject = new PactDslJsonBody()
            .stringType("message", "Error Message");
}
