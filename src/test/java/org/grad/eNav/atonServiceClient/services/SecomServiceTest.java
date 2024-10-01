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

package org.grad.eNav.atonServiceClient.services;

import org.grad.secom.core.exceptions.SecomValidationException;
import org.grad.secom.core.models.*;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.grad.secom.springboot3.components.SecomClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecomServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    SecomService secomService;

    /**
     * The Discovery Service mock.
     */
    @Mock
    SecomClient discoveryService;

    // Test Variables
    List<SearchObjectResult> instances;
    ResponseSearchObject responseSearchObject;
    List<SummaryObject> summaryObjects;
    GetSummaryResponseObject summaryResponseObject;
    GetResponseObject getResponseObject;
    DataResponseObject dataResponseObject;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Set the discovery service URL variable
        this.secomService.discoveryServiceUrl = "http://localhost:8444/v1/searchService";

        // Create a list of retrieved instances
        SearchObjectResult searchObjectResult1 = new SearchObjectResult();
        searchObjectResult1.setName("searchResult1");
        searchObjectResult1.setVersion("0.0.1");
        searchObjectResult1.setEndpointUri("http://localhost/");
        SearchObjectResult searchObjectResult2 = new SearchObjectResult();
        searchObjectResult2.setName("searchResult2");
        searchObjectResult2.setVersion("0.0.2");
        searchObjectResult2.setEndpointUri("http://localhost/");
        this.instances = Arrays.asList(searchObjectResult1, searchObjectResult2);

        // Create the response search object
        this.responseSearchObject = new ResponseSearchObject();
        this.responseSearchObject.setSearchServiceResult(this.instances);

        // Create a list of summary objects
        SummaryObject summaryObject1 = new SummaryObject();
        summaryObject1.setInfo_name("SummaryObject1");
        summaryObject1.setDataReference(UUID.randomUUID());
        summaryObject1.setContainerType(ContainerTypeEnum.S100_DataSet);
        SummaryObject summaryObject2 = new SummaryObject();
        summaryObject2.setInfo_name("SummaryObject2");
        summaryObject2.setDataReference(UUID.randomUUID());
        summaryObject2.setContainerType(ContainerTypeEnum.S100_DataSet);
        this.summaryObjects = Arrays.asList(summaryObject1, summaryObject2);

        // Create the summary response object
        this.summaryResponseObject = new GetSummaryResponseObject();
        summaryResponseObject.setSummaryObject(this.summaryObjects);

        // Load the S-125 dataset file
        final InputStream in = ClassLoader.getSystemResourceAsStream("s125-msg.xml");
        assert in != null;

        // Create the data reponse object
        this.dataResponseObject = new DataResponseObject();
        this.dataResponseObject.setData(in.readAllBytes());

        // Create the get response object
        this.getResponseObject = new GetResponseObject();
        this.getResponseObject.setDataResponseObject(Collections.singletonList(this.dataResponseObject));
    }

    /**
     * That that during its initialisation the SECOM service will construct the
     * SECOM discovery service client.
     */
    @Test
    void testInit() {
        // Perform the service call
        this.secomService.init();

        // Make sure the discovery service was initialise properly
        assertNotNull(this.secomService.discoveryService);
    }

    /**
     * That that during its termination the SECOM service will destroy the
     * SECOM discovery service client.
     */
    @Test
    void testDestroy() {
        // Perform the service call
        this.secomService.init();
        this.secomService.destroy();

        // Make sure the discovery service was destroyed properly
        assertNull(this.secomService.discoveryService);
    }

    /**
     * Test that we can use the discovery service to retrieve registered S-125
     * AtoN services using an unpaged search.
     */
    @Test
    void testGetRegisteredServicesUnPaged() {
        // Mock the search service response
        doReturn(Optional.of(this.responseSearchObject)).when(this.discoveryService).searchService(any(), eq(0), eq(SecomService.MAX_UNPAGED_RESULTS_NO));

        // Perform the service call
        List<SearchObjectResult> result = this.secomService.getRegisteredServices("test", Pageable.unpaged());

        // Make sure the client seems OK
        assertNotNull(result);
        assertEquals(this.instances.size(), result.size());
        // Check all returned instances
        for(int i=0; i<result.size(); i++) {
            assertNotNull(result.get(i));
            assertEquals(this.instances.get(i).getName(), result.get(i).getName());
        }
    }

    /**
     * Test that we can use the discovery service to retrieve registered S-125
     * AtoN services using a paged search.
     */
    @Test
    void testGetRegisteredServicesPaged() {
        // Mock the search service response
        doReturn(Optional.of(this.responseSearchObject)).when(this.discoveryService).searchService(any(), eq(10), eq(1));

        // Perform the service call
        List<SearchObjectResult> result = this.secomService.getRegisteredServices("test",PageRequest.of(10,1));

        // Make sure the client seems OK
        assertNotNull(result);
        assertEquals(this.instances.size(), result.size());
        // Check all returned instances
        for(int i=0; i<result.size(); i++) {
            assertNotNull(result.get(i));
            assertEquals(this.instances.get(i).getName(), result.get(i).getName());
        }
    }

    /**
     * Test that we can retrieve the available datasets from a SECOM service
     * using its summary interface and an unpaged query.
     */
    @Test
    void testGetServiceDatasetsUnPaged() {
        // Mock the S-125 AtoN service response
        SecomClient secomClient = mock(SecomClient.class);
        doReturn(Optional.of(this.summaryResponseObject)).when(secomClient).getSummary(any(), any(), any(), any(), any(), any(), any(), any(), any());
        doReturn(secomClient).when(this.secomService).getClient(eq("mrn"));

        // Perform the service call
        List<SummaryObject> result = this.secomService.getServiceDatasets("mrn", Pageable.unpaged());

        // Make sure the client seems OK
        assertNotNull(result);
        assertEquals(this.summaryObjects.size(), result.size());
        // Check all returned instances
        for(int i=0; i<result.size(); i++) {
            assertNotNull(result.get(i));
            assertEquals(this.summaryObjects.get(i).getDataReference(), result.get(i).getDataReference());
            assertEquals(this.summaryObjects.get(i).getInfo_name(), result.get(i).getInfo_name());
        }
    }

    /**
     * Test that we can retrieve the available datasets from a SECOM service
     * using its summary interface and a paged query.
     */
    @Test
    void testGetServiceDatasetsPaged() {
        // Mock the S-125 AtoN service response
        SecomClient secomClient = mock(SecomClient.class);
        doReturn(Optional.of(this.summaryResponseObject)).when(secomClient).getSummary(any(), any(), any(), any(), any(), any(), any(), any(), any());
        doReturn(secomClient).when(this.secomService).getClient(eq("mrn"));

        // Perform the service call
        List<SummaryObject> result = this.secomService.getServiceDatasets("mrn", PageRequest.of(10,1));

        // Make sure the client seems OK
        assertNotNull(result);
        assertEquals(this.summaryObjects.size(), result.size());
        // Check all returned instances
        for(int i=0; i<result.size(); i++) {
            assertNotNull(result.get(i));
            assertEquals(this.summaryObjects.get(i).getDataReference(), result.get(i).getDataReference());
            assertEquals(this.summaryObjects.get(i).getInfo_name(), result.get(i).getInfo_name());
        }
    }

    /**
     * Test that we can retrieve the content of the available datasets from a
     *  SECOM service using its get interface and an unpaged query.
     */
    @Test
    void testGetServiceDatasetContentUnpaged() {
        // First select a UUID
        UUID uuid = UUID.randomUUID();

        // Mock the S-125 AtoN service response
        SecomClient secomClient = mock(SecomClient.class);
        doReturn(Optional.of(this.getResponseObject)).when(secomClient).get(eq(uuid), any(), any(), any(), any(), any(), any(), any(), any(), any());
        doReturn(secomClient).when(this.secomService).getClient(eq("mrn"));

        // Perform the service call
        List<?> result = this.secomService.getServiceDatasetContent("mrn", uuid.toString(), SECOM_DataProductType.S125, null, null, null, null, null, Pageable.unpaged());

        // Make sure the client seems OK
        assertNotNull(result);
        assertEquals(1, result.size());
        // Check all returned instances
        for(int i=0; i<result.size(); i++) {
            assertNotNull(result.get(i));
        }
    }

    /**
     * Test that we can retrieve the content of the available datasets from a
     *  SECOM service using its get interface and a paged query.
     */
    @Test
    void testGetServiceDatasetContentPaged() {
        // First select a UUID
        UUID uuid = UUID.randomUUID();

        // Mock the S-125 AtoN service response
        SecomClient secomClient = mock(SecomClient.class);
        doReturn(Optional.of(this.getResponseObject)).when(secomClient).get(eq(uuid), any(), any(), any(), any(), any(), any(), any(), any(), any());
        doReturn(secomClient).when(this.secomService).getClient(eq("mrn"));

        // Perform the service call
        List<?> result = this.secomService.getServiceDatasetContent("mrn", uuid.toString(), SECOM_DataProductType.S125, null, null, null, null, null, PageRequest.of(10,1));

        // Make sure the client seems OK
        assertNotNull(result);
        assertEquals(1, result.size());
        // Check all returned instances
        for(int i=0; i<result.size(); i++) {
            assertNotNull(result.get(i));
        }
    }

    /**
     * Test that the SECOM service will contact the SECOM discovery service
     * allocated to it, to discover the requested clients based on their MRNs.
     */
    @Test
    void testGetClient() {
        // And mock a SECOM discovery service client
        this.secomService.discoveryService = mock(SecomClient.class);
        doReturn(Optional.of(this.responseSearchObject)).when(this.secomService.discoveryService).searchService(any(), any(), any());

        // Perform the service call
        SecomClient result = this.secomService.getClient("urn:mrn:org:test");

        // Make sure the client seems OK
        assertNotNull(result);
    }

    /**
     * Test that the SECOM service will contact the SECOM discovery service
     * allocated to it, to discover the requested clients based on their MRNs.
     * If the discovered URL does not seem valid, a SecomValidationException
     * will be thrown.
     */
    @Test
    void testGetClientBrokenUrl() {
        // Break the URL of the latest instance
        this.responseSearchObject.getSearchServiceResult().get(1).setEndpointUri("a broken URL");

        // And mock a SECOM discovery service client
        this.secomService.discoveryService = mock(SecomClient.class);
        doReturn(Optional.of(this.responseSearchObject)).when(this.secomService.discoveryService).searchService(any(), any(), any());

        // Perform the service call
        assertThrows(SecomValidationException.class, () -> this.secomService.getClient("urn:mrn:org:test"));
    }

}