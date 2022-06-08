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

package org.grad.eNav.atonServiceClient.services;

import lombok.extern.slf4j.Slf4j;
import org.grad.secom.models.SubscriptionRequestObject;
import org.grad.secom.models.SubscriptionResponseObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

import static org.grad.secom.interfaces.SubscriptionInterface.SUBSCRIPTION_INTERFACE_PATH;

/**
 * The SECOM Subscription Service Class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class SubscriptionService {

    /**
     * The Application MRN Information.
     */
    @Value("${gla.rad.aton-service-client.info.mrn:}")
    private String appMrn;

    /**
     * Creates a new subscription to a SECOM service, searched and found using
     * the SECOM Service Registry, if specified to the service (otherwise this
     * operation will do nothing), and will await for the confirmation
     * notification so that it informs the requesting client.
     *
     * @param subscriptionRequestObject the subscription request object
     */
    public void createSubscription(SubscriptionRequestObject subscriptionRequestObject) {
        Optional<SubscriptionResponseObject> response = WebClient.create("http://localhost:8766/api/secom")
                .post()
                .uri(SUBSCRIPTION_INTERFACE_PATH)
                .header("MRN", this.appMrn)
                .body(BodyInserters.fromValue(subscriptionRequestObject))
                .retrieve()
                .bodyToMono(SubscriptionResponseObject.class)
                .blockOptional();

        // Print the output if present
        response.ifPresent(result -> {
            this.log.info(result.getResponseText());
        });
    }

}
