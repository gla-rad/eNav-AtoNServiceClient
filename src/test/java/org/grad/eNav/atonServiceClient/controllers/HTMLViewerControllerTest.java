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

import org.grad.eNav.atonServiceClient.services.SecomService;
import org.grad.eNav.atonServiceClient.services.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = HTMLViewerController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class HTMLViewerControllerTest {

    /**
     * The Mock MVC.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * The SECOM Service.
     */
    @MockBean
    SecomService secomService;

    /**
     * The Subscription Service.
     */
    @MockBean
    SubscriptionService subscriptionService;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {

    }

    /**
     * Test that we can access the main index HTML page.
     */
    @Test
    void testGetIndex() throws Exception {

        // Perform the MVC request
        this.mockMvc.perform(get("/index")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    /**
     * Test that we can access the about HTML page.
     */
    @Test
    void testGetAbout() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(get("/about")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf())
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

}