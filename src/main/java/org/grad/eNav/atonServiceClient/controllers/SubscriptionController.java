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

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.services.SubscriptionService;
import org.grad.secom.core.models.RemoveSubscriptionResponseObject;
import org.grad.secom.core.models.SubscriptionRequestObject;
import org.grad.secom.core.models.SubscriptionResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * The REST Controller for managing subscriptions.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/subscription")
@Slf4j
public class SubscriptionController {

    /**
     * The Subscription Service.
     */
    @Autowired
    SubscriptionService subscriptionService;

    /**
     * POST /api/subscription/{mrn} : Creates a new subscription on the service
     * specified by the MRN identifier, and starts receiving the matching data.
     *
     * @param mrn the MRN of the service to subscribe to
     * @param subscriptionRequestObject the SECOM subscription request object
     * @return the ResponseEntity with status 200 (OK) and the list of datasets in body
     */
    @PostMapping(value = "/{mrn}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SubscriptionResponseObject createSubscription(@PathVariable String mrn, @RequestBody SubscriptionRequestObject subscriptionRequestObject) {
        log.debug("REST request to initiate a subscription with {}", mrn);
        return this.subscriptionService.createSubscription(mrn, subscriptionRequestObject);
    }

    /**
     * POST /api/subscription/{mrn} : Creates a new subscription on the service
     * specified by the MRN identifier, and starts receiving the matching data.
     *
     * @param mrn the MRN of the service to remove the subscription from
     * @return the ResponseEntity with status 200 (OK) and the list of datasets in body
     */
    @DeleteMapping(value = "/{mrn}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RemoveSubscriptionResponseObject removeSubscription(@PathVariable String mrn) {
        log.debug("REST request to terminate a subscription with {}", mrn);
        return this.subscriptionService.removeSubscription(mrn);
    }

}
