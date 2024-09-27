package org.grad.eNav.atonServiceClient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.grad.eNav.atonServiceClient.services.SecomService;
import org.grad.secom.core.models.SearchObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = DataProductController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class DataProductControllerTest {

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
     * The Secom Service mock.
     */
    @MockBean
    SecomService secomService;

    // Test Variables
    private List<SearchObjectResult> instances;

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
        MvcResult mvcResult = this.mockMvc.perform(get("/api/data_product/test/services")
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

}