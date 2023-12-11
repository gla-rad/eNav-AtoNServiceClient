/*
 * Copyright (c) 2021 GLA Research and Development Directorate
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

package org.grad.eNav.atonServiceClient.pacts.utils;

import au.com.dius.pact.consumer.dsl.DslPart;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


/**
 * Helper that tests mapping of models in pacts.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class PactTestHelper {

    /**
     * Test pact dto mapping.
     *
     * @param dslPart  the dsl part
     * @param dtoClass the dto class
     * @throws IOException the IO exception the occurred
     */
    public static void testPactDtoMapping(DslPart dslPart, Class<?> dtoClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        //If there are any mapping issues, appropriate module could be the solution
        mapper.registerModule(new JavaTimeModule());
        String pactJson = dslPart.getBody().toString();
        String dtoJson = mapper.writeValueAsString(mapper.readValue(pactJson, dtoClass));
        assertEquals(mapper.readTree(dtoJson), mapper.readTree(pactJson));
    }


}


