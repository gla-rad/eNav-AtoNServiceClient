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

package org.grad.eNav.atonServiceClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * The Test Configuration.
 */
@Configuration
public class TestingConfiguration {

    /**
     * Builds the object mapper used throughout the SECOM pact tests, configured
     * to omit null values so that the serialised output matches the pact DSL
     * definitions.
     *
     * @return the configured test object mapper
     */
    public static ObjectMapper testObjectMapper() {
        return JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl
                        .withContentInclusion(JsonInclude.Include.NON_NULL)
                        .withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
    }

}
