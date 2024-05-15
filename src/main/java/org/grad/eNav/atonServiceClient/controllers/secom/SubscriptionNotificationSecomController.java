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

package org.grad.eNav.atonServiceClient.controllers.secom;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.services.SubscriptionService;
import org.grad.secom.core.interfaces.SubscriptionNotificationSecomInterface;
import org.grad.secom.core.models.SubscriptionNotificationObject;
import org.grad.secom.core.models.SubscriptionNotificationResponseObject;
import org.grad.secom.core.models.enums.SubscriptionEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.ws.rs.Path;

@Component
@Path("/")
@Validated
@Slf4j
public class SubscriptionNotificationSecomController implements SubscriptionNotificationSecomInterface {

    /**
     * Attach the web-socket as a simple messaging template
     */
    @Autowired
    SimpMessagingTemplate webSocket;

    /**
     * The Subscription Service.
     */
    @Autowired
    SubscriptionService subscriptionService;

    /**
     * POST /v1/subscription/notification : The interface receives notifications
     * when a subscription is created or removed by the information provider.
     *
     * @param subscriptionNotificationObject the subscription notification request object
     * @return the subscription notification response object
     */
    @Tag(name = "SECOM")
    public SubscriptionNotificationResponseObject subscriptionNotification(@Valid SubscriptionNotificationObject subscriptionNotificationObject) {
        log.debug("Received SECOM notification {} for subscription with identifier {}",
                subscriptionNotificationObject.getEventEnum(),
                subscriptionNotificationObject.getSubscriptionIdentifier());

        // Initialise a response
        SubscriptionNotificationResponseObject subscriptionNotificationResponseObject = new SubscriptionNotificationResponseObject();

        // If the subscription event is a removal, we might need to also need to
        // remove the subscription from the current active list.
        if(subscriptionNotificationObject.getEventEnum() == SubscriptionEventEnum.SUBSCRIPTION_REMOVED) {
            this.subscriptionService.deleteSubscription(subscriptionNotificationObject.getSubscriptionIdentifier());
            subscriptionNotificationResponseObject.setResponseText(String.format(
                    "The subscription with identifier %s has been removed by the service",
                    subscriptionNotificationObject.getSubscriptionIdentifier()
            ));
        }

        // Send the subscription notification to the web-socket
        this.webSocket.convertAndSend(
                "/topic/secom/subscription/" + (subscriptionNotificationObject.getEventEnum() == SubscriptionEventEnum.SUBSCRIPTION_CREATED ? "created" : "removed"),
                subscriptionNotificationObject.getSubscriptionIdentifier()
        );

        // Return the response
        return subscriptionNotificationResponseObject;
    }

}
