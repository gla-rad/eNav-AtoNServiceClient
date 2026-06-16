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

package org.grad.eNav.atonServiceClient.controllers.secom;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.services.SubscriptionService;
import org.grad.secomv2.core.interfaces.SubscriptionNotificationServiceInterface;
import org.grad.secomv2.core.models.*;
import org.grad.secomv2.core.models.enums.SubscriptionEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Component
@Validated
@Slf4j
public class SubscriptionNotificationSecomController implements SubscriptionNotificationServiceInterface {

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
     * POST /v2/subscription/notification : The interface receives notifications
     * when a subscription is created or removed by the information provider.
     *
     * @param subscriptionNotificationObject the subscription notification request object
     * @return the subscription notification response object
     */
    @Tag(name = "SECOM")
    public ResponseEntity<SubscriptionNotificationResponseObject> subscriptionNotification(@Valid SubscriptionNotificationObject subscriptionNotificationObject) {
        EnvelopeSubscriptionNotificationObject envelopeSubscriptionNotificationObject = subscriptionNotificationObject.getEnvelope();

        log.debug("Received SECOM notification {} for subscription with identifier {}",
                envelopeSubscriptionNotificationObject.getEventEnum(),
                envelopeSubscriptionNotificationObject.getSubscriptionIdentifier());

        // Initialise a response
        SubscriptionNotificationResponseObject subscriptionNotificationResponseObject = new SubscriptionNotificationResponseObject();

        // If the subscription event is a removal, we might need to also need to
        // remove the subscription from the current active list.
        if(envelopeSubscriptionNotificationObject.getEventEnum() == SubscriptionEventEnum.SUBSCRIPTION_REMOVED) {
            this.subscriptionService.deleteSubscription(envelopeSubscriptionNotificationObject.getSubscriptionIdentifier());
            subscriptionNotificationResponseObject.setMessage(String.format(
                    "The subscription with identifier %s has been removed by the service",
                    envelopeSubscriptionNotificationObject.getSubscriptionIdentifier()
            ));
        }

        // Send the subscription notification to the web-socket
        this.webSocket.convertAndSend(
                "/topic/secom/subscription/" + (envelopeSubscriptionNotificationObject.getEventEnum() == SubscriptionEventEnum.SUBSCRIPTION_CREATED ? "created" : "removed"),
                envelopeSubscriptionNotificationObject.getSubscriptionIdentifier()
        );

        // Return the response
        return ResponseEntity.ok(subscriptionNotificationResponseObject);
    }

}
