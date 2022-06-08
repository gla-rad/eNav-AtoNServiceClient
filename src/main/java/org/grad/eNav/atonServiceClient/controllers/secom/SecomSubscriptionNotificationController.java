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

package org.grad.eNav.atonServiceClient.controllers.secom;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.grad.secom.interfaces.SubscriptionNotificationInterface;
import org.grad.secom.models.SubscriptionNotificationObject;
import org.grad.secom.models.SubscriptionNotificationResponseObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/secom")
@Validated
@Slf4j
public class SecomSubscriptionNotificationController implements SubscriptionNotificationInterface {

    /**
     * POST /api/secom/v1/subscription/notification : Accepts the notifications
     * on the status of the generated subscriptions.
     *
     * @param subscriptionNotificationObject the subscription notification object
     * @return the subscription notification response
     */
    @Override
    @Tag(name = "SECOM")
    @GetMapping(value = SUBSCRIPTION_NOTIFICATION_INTERFACE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubscriptionNotificationResponseObject> subscriptionNotification(@Valid SubscriptionNotificationObject subscriptionNotificationObject) {
        return null;
    }

}
