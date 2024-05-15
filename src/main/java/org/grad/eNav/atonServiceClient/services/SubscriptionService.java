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

package org.grad.eNav.atonServiceClient.services;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.models.domain.Subscription;
import org.grad.eNav.atonServiceClient.repos.SubscriptionRepo;
import org.grad.secom.core.exceptions.SecomNotFoundException;
import org.grad.secom.core.models.RemoveSubscriptionObject;
import org.grad.secom.core.models.RemoveSubscriptionResponseObject;
import org.grad.secom.core.models.SubscriptionRequestObject;
import org.grad.secom.core.models.SubscriptionResponseObject;
import org.grad.secom.springboot3.components.SecomClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * The Subscription Service.
 * <p/>
 * It was deemed that having a specific service to control subscriptions, even
 * in the case of this simple client might be beneficial for the whole
 * demonstration operation and removes any CORS requirements.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class SubscriptionService {

    /**
     * The SECOM Service.
     */
    @Autowired
    SecomService secomService;

    /**
     * The Subscription Repo.
     */
    @Autowired
    SubscriptionRepo subscriptionRepo;

    /**
     * Retrieves the currently active subscription. For the time being this
     * demo service will support one subscription at a time, even if multiple
     * once can be accepted by the database. So this function will return this
     * subscription if it exists.
     *
     * @return the currently active subscription
     */
    public Optional<Subscription> getActiveSubscription() {
        return this.subscriptionRepo
                .findAll()
                .stream()
                .findFirst();
    }

    /**
     * Retrieves a subscription based on the provided UUID identifier and
     * returns it to the calling function. This operation will throw an error
     * if the requested identifier is not found.
     *
     * @param identifier The UUID identifier of the subscription
     * @return the matching subscription object
     */
    public Subscription getSubscription(UUID identifier) {
        return this.subscriptionRepo.findByIdentifier(identifier)
                .orElseThrow(NotFoundException::new);
    }

    /**
     * Deletes a subscription based on the provided UUID identifier. This
     * operation will throw an error if the requested identifier is not
     * found.
     *
     * @param identifier The UUID identifier of the subscription
     * @return the deleted subscription object
     */
    public Subscription deleteSubscription(UUID identifier) {
        // First get the subscription to make sure it's there
        final Subscription subscription = this.getSubscription(identifier);

        // Now delete it from the database
        this.subscriptionRepo.delete(subscription);

        // And return the object
        return subscription;
    }

    /**
     * Performs the subscription operation using the SECOM library client.
     * In practice this searches the service registry and then return a client
     * if it matches the provided MRN. This can then be used to perform the
     * SECOM subscription operation.
     *
     * @param mrn the MRN of the service to subscribe to
     * @param subscriptionRequestObject the SECOM subscription request object
     * @return the SECOM subscription response object
     */
    public SubscriptionResponseObject createClientSubscription(@NotNull String mrn, @NotNull SubscriptionRequestObject subscriptionRequestObject) {
        // Only support one subscription at a time
        this.getActiveSubscription()
                .map(Subscription::getMrn)
                .ifPresent(this::removeClientSubscription);

        // First search for the specified MRN in the service registry
        final SecomClient secomClient = secomService.getClient(mrn);

        // Initiate the subscription
        final SubscriptionResponseObject subscriptionResponseObject = secomClient.subscription(subscriptionRequestObject)
                .orElseThrow(() -> new SecomNotFoundException("The client for generating the subscription was not found in the service registry."));

        // Save the entry for future reference
        final Subscription subscription = new Subscription();
        subscription.setMrn(mrn);
        subscription.setIdentifier(subscriptionResponseObject.getSubscriptionIdentifier());
        subscriptionRepo.save(subscription);

        // Return the response
        return subscriptionResponseObject;
    }

    /**
     * Removes an already active subscription using the SECOM library client.
     * In practice this searches the service registry and then return a client
     * if it matches the provided MRN. This can then be used to perform the
     * SECOM remove-subscription operation.
     *
     * @param mrn the MRN of the service to remove the subscription from
     * @return the SECOM subscription response object
     */
    public RemoveSubscriptionResponseObject removeClientSubscription(@NotNull String mrn) {
        // The try to find any existing subscriptions
        final Subscription subscription = this.getActiveSubscription()
                .orElseThrow(() -> new RuntimeException("No active subscription detected to be removed."));

        // Search for the specified MRN in the service registry
        final SecomClient secomClient = secomService.getClient(mrn);

        // Terminate the subscription
        final RemoveSubscriptionObject removeSubscriptionObject = new RemoveSubscriptionObject();
        removeSubscriptionObject.setSubscriptionIdentifier(subscription.getIdentifier());
        final RemoveSubscriptionResponseObject removeSubscriptionResponseObject = secomClient.removeSubscription(removeSubscriptionObject)
                .orElseThrow(() -> new SecomNotFoundException("The client for removing the subscription was not found in the service registry."));

        // Inform the database about the change
        this.subscriptionRepo.delete(subscription);

        // And return the response
        return removeSubscriptionResponseObject;
    }

}
