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

package org.grad.eNav.atonServiceClient.controllers;

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.services.SubscriptionService;
import org.grad.secom.models.SubscriptionRequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * The SECOM Subscription Controller Class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/subscription")
@Validated
@Slf4j
public class SubscriptionController {

    /**
     * The Subscription Service.
     */
    @Autowired
    SubscriptionService subscriptionService;

    /**
     * POST /api/subscription : Receives and handles requests for subscription
     * on the SECOM-compliant AtoN Service. This needs to be already registered
     * with the SECOM-compliant service registry otherwise the request will
     * fail.
     *
     * @param subscriptionRequestObject the upload object
     * @return the subscription response
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> subscribe(@RequestBody @Valid SubscriptionRequestObject subscriptionRequestObject) {
        this.log.debug("Received request to generate subscription: {}", subscriptionRequestObject);

        // Try to create a new subscription
        this.subscriptionService.createSubscription(subscriptionRequestObject);

        // And return the response entity
        return ResponseEntity.ok()
                .build();
    }

    /**
     * DELETE /api/subscription : Deletes the existing subscription generated
     * and informs the AtoN service to remove its own subscription entry.
     *
     * @return the subscription response
     */
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> unsubscribe() {
        this.log.debug("Received request to delete the existing subscription");

        // And return the response entity
        return ResponseEntity.ok()
                .build();
    }

}
