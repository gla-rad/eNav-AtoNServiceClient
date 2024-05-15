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

package org.grad.eNav.atonServiceClient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.grad.eNav.atonServiceClient.services.SubscriptionService;
import org.grad.secom.core.models.RemoveSubscriptionResponseObject;
import org.grad.secom.core.models.SubscriptionRequestObject;
import org.grad.secom.core.models.SubscriptionResponseObject;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = SubscriptionController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class SubscriptionControllerTest {

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
     * The Subscription Service mock.
     */
    @MockBean
    SubscriptionService subscriptionService;

    // Test Variables
    private SubscriptionRequestObject subscriptionRequestObject;
    private SubscriptionResponseObject subscriptionResponseObject;
    private RemoveSubscriptionResponseObject removeSubscriptionResponseObject;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Initialise the subscription request object
        this.subscriptionRequestObject = new SubscriptionRequestObject();
        this.subscriptionRequestObject.setDataReference(UUID.randomUUID());
        this.subscriptionRequestObject.setContainerType(ContainerTypeEnum.S100_DataSet);
        this.subscriptionRequestObject.setDataProductType(SECOM_DataProductType.S125);

        // Initialise the subscription response object
        this.subscriptionResponseObject = new SubscriptionResponseObject();
        this.subscriptionResponseObject.setSubscriptionIdentifier(UUID.randomUUID());
        this.subscriptionResponseObject.setMessage("message");

        // Initialise the remove subscription response object
        this.removeSubscriptionResponseObject = new RemoveSubscriptionResponseObject();
        this.removeSubscriptionResponseObject.setMessage("remove message");
    }

    /**
     * Test that the AtoN Service Client REST interface can be successfully used
     * ti create new subscriptions.
     */
    @Test
    void testCreateSubscription() throws Exception {
        // Mock the subscription service to return a fixed result on an MRN
        doReturn(this.subscriptionResponseObject).when(this.subscriptionService).createClientSubscription(eq("urn:mrn:service"), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(post("/api/subscription/urn:mrn:service")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(this.subscriptionRequestObject)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        SubscriptionResponseObject result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SubscriptionResponseObject.class);
        assertNotNull(result);
        assertEquals(this.subscriptionResponseObject.getSubscriptionIdentifier(), result.getSubscriptionIdentifier());
        assertEquals(this.subscriptionResponseObject.getMessage(), result.getMessage());
    }

    /**
     * Test that the AtoN Service Client REST interface can be successfully used
     * ti remove existing subscriptions.
     */
    @Test
    void testRemoveSubscription() throws Exception {
        // Mock the subscription service to return a fixed result on an MRN
        doReturn(this.removeSubscriptionResponseObject).when(this.subscriptionService).removeClientSubscription(eq("urn:mrn:service"));

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(delete("/api/subscription/urn:mrn:service"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        RemoveSubscriptionResponseObject result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RemoveSubscriptionResponseObject.class);
        assertNotNull(result);
        assertEquals(this.removeSubscriptionResponseObject.getMessage(), result.getMessage());
    }

}