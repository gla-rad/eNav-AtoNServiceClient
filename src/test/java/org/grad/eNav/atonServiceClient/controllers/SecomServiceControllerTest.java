package org.grad.eNav.atonServiceClient.controllers;

import _int.iho.s125.gml.cs0._1.AidsToNavigationType;
import _int.iho.s125.gml.cs0._1.impl.AidsToNavigationTypeImpl;
import _int.iho.s125.gml.cs0._1.impl.VirtualAISAidToNavigationImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.grad.eNav.atonServiceClient.models.domain.SignedDatasetContent;
import org.grad.eNav.atonServiceClient.services.SecomService;
import org.grad.secom.core.models.*;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = SecomServiceController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class SecomServiceControllerTest {

    /**
     * The Mock MVC.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * The JSON Object Mapper.
     */
    @Autowired
    ObjectMapper objectMapper;

    /**
     * The Web-Socket mock.
     */
    @MockBean
    SimpMessagingTemplate webSocket;

    /**
     * The Secom Service mock.
     */
    @MockBean
    SecomService secomService;

    // Test Variables
    private List<SearchObjectResult> instances;
    private List<SummaryObject> summaryObjects;
    private List<AidsToNavigationType> atons;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
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

        // Create a list of AtoN objects
        AidsToNavigationTypeImpl aton1 = new VirtualAISAidToNavigationImpl();
        aton1.setId("ID1");
        aton1.setIdCode("idCode1");
        AidsToNavigationTypeImpl aton2 = new VirtualAISAidToNavigationImpl();
        aton2.setId("ID1");
        aton2.setIdCode("idCode1");
        this.atons = Arrays.asList(aton1, aton2);
    }

    /**
     * Test that the AtoN Service Client REST interface can be successfully used
     * to get the list of S-125 AtoN services currently registered with the
     * associated MSR.
     */
    @Test
    void testGetRegisteredServices() throws Exception {
        // Mock the subscription service to return a fixed result on an MRN
        doReturn(this.instances).when(this.secomService).getRegisteredServices(eq("test"), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/secom_service?dataProductType=test")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        SearchObjectResult[] result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SearchObjectResult[].class);
        assertNotNull(result);
        assertEquals(this.instances.size(), result.length);
        // Check all returned instances
        for(int i=0; i<result.length; i++) {
            assertNotNull(result[i]);
            assertEquals(this.instances.get(i).getName(), result[i].getName());
        }
    }

    /**
     * Test that the AtoN Service Client REST interface can be successfully used
     * to get the list of S-125 datasets currently provided by a specific S-125
     * AtoN service, identified by its MRN.
     */
    @Test
    void testGetSecomDatasets() throws Exception {
        // Mock the subscription service to return a fixed result on an MRN
        doReturn(this.summaryObjects).when(this.secomService).getServiceDatasets(eq("mrn"), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/secom_service/mrn/summary")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        SummaryObject[] result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SummaryObject[].class);
        assertNotNull(result);
        assertEquals(this.summaryObjects.size(), result.length);
        // Check all returned instances
        for(int i=0; i<result.length; i++) {
            assertNotNull(result[i]);
            assertEquals(this.summaryObjects.get(i).getInfo_name(), result[i].getInfo_name());
        }
    }

    /**
     * Test that the SECOM Service Client REST interface can be successfully used
     * to get the contents of S-100 datasets currently provided by a generic
     * SECOM service, identified by its MRN.
     */
    @Test
    void testGetSecomDatasetContent() throws Exception {
        // First select a UUID
        UUID uuid = UUID.randomUUID();

        // Mock the subscription service to return a fixed result on an MRN
        final InputStream in = ClassLoader.getSystemResourceAsStream("s125-msg.xml");
        final SignedDatasetContent signedDatasetContent = new SignedDatasetContent();
        signedDatasetContent.setContent(in.readAllBytes());
        doReturn(Collections.singletonList(signedDatasetContent)).when(this.secomService).getServiceDatasetContent(eq("mrn"), eq(uuid.toString()), any(), any(), any(), any(), any(), any(), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/secom_service/mrn/content?dataReference=" + uuid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * Test that the SECOM Service Client REST interface can be successfully used
     * to get the contents of S-125 datasets currently provided by a specific
     * SECOM AtoN service, identified by its MRN.
     */
    @Test
    void testGetSecomDatasetContentS125() throws Exception {
        // First select a UUID
        UUID uuid = UUID.randomUUID();

        // Mock the subscription service to return a fixed result on an MRN
        final InputStream in = ClassLoader.getSystemResourceAsStream("s125-msg.xml");
        final SignedDatasetContent signedDatasetContent = new SignedDatasetContent();
        signedDatasetContent.setContent(in.readAllBytes());
        doReturn(Collections.singletonList(signedDatasetContent)).when(this.secomService).getServiceDatasetContent(eq("mrn"), eq(uuid.toString()), eq(SECOM_DataProductType.S125), any(), any(), any(), any(), any(), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/secom_service/mrn/content?dataProductType=S125&dataReference=" + uuid)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

}