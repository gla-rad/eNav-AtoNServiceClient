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

package org.grad.eNav.atonServiceClient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.grad.secom.components.ContainerTypeConverterProvider;
import org.grad.secom.components.LocalDateTimeConverterProvider;
import org.grad.secom.components.ObjectMapperProvider;
import org.grad.secom.components.SecomExceptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JAX-RS application
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@ApplicationPath("/api/secom/")
public class JaxrsApplication extends Application {

    /**
     * Initialise the SECOM exception mapper.
     *
     * @return the SECOM exception mapper bean
     */
    @Bean
    SecomExceptionMapper secomExceptionMapper() {
        return new SecomExceptionMapper();
    }

    /**
     * Initialise the SECOM object mapping operation with the Springboot object
     * mapper.
     *
     * @param objectMapper the autowired object mapper
     * @return the object mapper provider
     */
    @Bean
    ObjectMapperProvider objectMapperProvider(@Autowired ObjectMapper objectMapper) {
        return new ObjectMapperProvider(objectMapper);
    }

    /**
     * Initialise the ContainerType Converter Provider bean.
     *
     * @return the ContainerType Converter Provider bean
     */
    @Bean
    ContainerTypeConverterProvider containerTypeConverterProvider() {
        return new ContainerTypeConverterProvider();
    }

    /**
     * Initialise the LocalDateTime Converter Provider bean.
     *
     * @return the LocalDateTime Converter Provider bean
     */
    @Bean
    LocalDateTimeConverterProvider localDateTimeConverterProvider() {
        return new LocalDateTimeConverterProvider();
    }

    /**
     * Register the required classes to Swagger.
     *
     * @return the set of classes to be registered.
     */
    @Override
    public Set<Class<?>> getClasses() {
        return Stream.of(OpenApiResource.class, AcceptHeaderOpenApiResource.class).collect(Collectors.toSet());
    }

}
