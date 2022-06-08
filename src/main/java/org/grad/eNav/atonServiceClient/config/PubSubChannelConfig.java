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

import org.grad.eNav.atonServiceClient.components.PubSubErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;

/**
 * The PubSubChannelConfig Class
 *
 * This class establishes a publish-subscribe channel when the internal
 * components of this application can exchange data messages. This will
 * be used to transfer the incoming radar data to various consumers.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Configuration
@EnableIntegration
public class PubSubChannelConfig {

    /**
     * The Pub-Sub Error Handler.
     */
    @Autowired
    PubSubErrorHandler pubSubErrorHandler;

    /**
     * Defining a publication publish-subscribe Spring Integration channel to
     * exchange the incoming S-125 and data between the application components.
     *
     * @return The publish-subscribe channel for the incoming S-125 data
     */
    @Bean
    public PublishSubscribeChannel publishSubscribeChannel() {
        PublishSubscribeChannel pubsubChannel = new PublishSubscribeChannel();
        pubsubChannel.setErrorHandler(this.pubSubErrorHandler);
        return pubsubChannel;
    }

}
