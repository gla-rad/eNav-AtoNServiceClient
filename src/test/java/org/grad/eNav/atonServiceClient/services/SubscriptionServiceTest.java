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

import jakarta.ws.rs.NotFoundException;
import org.grad.eNav.atonServiceClient.models.domain.Subscription;
import org.grad.eNav.atonServiceClient.repos.SubscriptionRepo;
import org.grad.secom.core.models.RemoveSubscriptionResponseObject;
import org.grad.secom.core.models.SubscriptionRequestObject;
import org.grad.secom.core.models.SubscriptionResponseObject;
import org.grad.secom.springboot3.components.SecomClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    SubscriptionService subscriptionService;

    /**
     * The SECOM Service mock.
     */
    @Mock
    SecomService secomService;

    /**
     * The Subscription Repo mock.
     */
    @Mock
    SubscriptionRepo subscriptionRepo;

    // Test Variables
    private SecomClient secomClient;
    private SubscriptionResponseObject subscriptionResponseObject;
    private RemoveSubscriptionResponseObject removeSubscriptionResponseObject;
    private Subscription subscription;
    private List<Subscription> subscriptions;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Mock the secom client
        this.secomClient = mock(SecomClient.class);

        // Initialise the subscription response object
        this.subscriptionResponseObject = new SubscriptionResponseObject();
        this.subscriptionResponseObject.setSubscriptionIdentifier(UUID.randomUUID());
        this.subscriptionResponseObject.setMessage("message");

        // Initialise the remove subscription response object
        this.removeSubscriptionResponseObject = new RemoveSubscriptionResponseObject();
        this.removeSubscriptionResponseObject.setMessage("remove message");

        // Initialise the subscription list - for now use only one subscription
        this.subscription = new Subscription();
        subscription.setId(BigInteger.ONE);
        subscription.setMrn("urn:mrn:service");
        subscription.setIdentifier(UUID.randomUUID());
        this.subscriptions = Collections.singletonList(this.subscription);
    }

    /**
     * Test that we can successfully retrieve an active subscription if this
     * exists. In the current software version only maximum one subscription can
     * be present at any point in time.
     */
    @Test
    void testGetActiveSubscription() {
        // Mock the repository response
        doReturn(this.subscriptions).when(this.subscriptionRepo).findAll();

        // Perform the service call
        Optional<Subscription> result =this.subscriptionService.getActiveSubscription();

        // Make sure the result seems OK
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(this.subscription.getId(), result.get().getId());
        assertEquals(this.subscription.getMrn(), result.get().getMrn());
        assertEquals(this.subscription.getIdentifier(), result.get().getIdentifier());
    }

    /**
     * Test that we can successfully retrieve a subscription based on it's UUID
     * identifier.
     */
    @Test
    void testGetSubscription() {
        // Mock the repository response
        doReturn(Optional.of(this.subscription)).when(this.subscriptionRepo).findByIdentifier(any());

        // Perform the service call
        Subscription result =this.subscriptionService.getSubscription(UUID.randomUUID());

        // Make sure the result seems OK
        assertNotNull(result);
        assertEquals(this.subscription.getId(), result.getId());
        assertEquals(this.subscription.getMrn(), result.getMrn());
        assertEquals(this.subscription.getIdentifier(), result.getIdentifier());
    }

    /**
     * Test that if the specified UUID identifier is not found an exception
     * will be thrown when we attempt to retrieve a subscription.
     */
    @Test
    void testGetSubscriptionNotFound() {
        // Mock the repository response
        doReturn(Optional.empty()).when(this.subscriptionRepo).findByIdentifier(any());

        // Perform the service call
        assertThrows(NotFoundException.class, () -> this.subscriptionService.getSubscription(UUID.randomUUID()));
    }

    /**
     * Test that we can successfully delete a subscription based on it's UUID
     * identifier.
     */
    @Test
    void testDeleteSubscription() {
        // Mock the repository response
        doReturn(Optional.of(this.subscription)).when(this.subscriptionRepo).findByIdentifier(any());
        doNothing().when(this.subscriptionRepo).delete(any());

        // Perform the service call
        Subscription result =this.subscriptionService.deleteSubscription(UUID.randomUUID());

        // Make sure the result seems OK
        assertNotNull(result);
        assertEquals(this.subscription.getId(), result.getId());
        assertEquals(this.subscription.getMrn(), result.getMrn());
        assertEquals(this.subscription.getIdentifier(), result.getIdentifier());
    }

    /**
     * Test that if the specified UUID identifier is not found an exception
     * will be thrown when we attempt to delete a subscription.
     */
    @Test
    void testDeleteSubscriptionNotFound() {
        // Mock the repository response
        doReturn(Optional.empty()).when(this.subscriptionRepo).findByIdentifier(any());

        // Perform the service call
        assertNull(this.subscriptionService.deleteSubscription(UUID.randomUUID()));
    }

    /**
     * Test that we can successfully create new subscriptions. In this operation
     * of the demonstration client, we only allow one subscription.
     */
    @Test
    void testCreateClientSubscription() {
        // Mock the internal class - no other subscription exists
        doReturn(this.secomClient).when(this.secomService).getClient(any());
        doReturn(Optional.of(this.subscriptionResponseObject)).when(this.secomClient).subscription(any());
        doReturn(Optional.empty()).when(this.subscriptionService).getActiveSubscription();

        // Perform the service call
        SubscriptionResponseObject result = this.subscriptionService.createClientSubscription("urn:mrn:service", new SubscriptionRequestObject());

        // Make sure the result seems OK
        assertNotNull(result);
        assertEquals(this.subscriptionResponseObject.getSubscriptionIdentifier(), result.getSubscriptionIdentifier());
        assertEquals(this.subscriptionResponseObject.getMessage(), result.getMessage());

        // Ensure we did not have to remove any previous subscriptions
        verify(this.subscriptionService, never()).removeClientSubscription(any());
    }

    /**
     * Test that we can successfully create new subscriptions. In this operation
     * of the demonstration client, we only allow one subscription. In case we
     * already have an existing subscription, we should first remove that.
     */
    @Test
    void testCreateClientSubscriptionWithExisting() {
        // Mock the internal class - no other subscription exists
        doReturn(this.secomClient).when(this.secomService).getClient(any());
        doReturn(Optional.of(this.subscriptionResponseObject)).when(this.secomClient).subscription(any());
        doReturn(Optional.of(this.subscription)).when(this.subscriptionService).getActiveSubscription();
        doReturn(this.removeSubscriptionResponseObject).when(this.subscriptionService).removeClientSubscription(any());

        // Perform the service call
        SubscriptionResponseObject result = this.subscriptionService.createClientSubscription("urn:mrn:service", new SubscriptionRequestObject());

        // Make sure the result seems OK
        assertNotNull(result);
        assertEquals(this.subscriptionResponseObject.getSubscriptionIdentifier(), result.getSubscriptionIdentifier());
        assertEquals(this.subscriptionResponseObject.getMessage(), result.getMessage());

        // Ensure we did not have to remove any previous subscriptions
        verify(this.subscriptionService, times(1)).removeClientSubscription(any());
    }

    /**
     * Test that we can successfully remove existing subscriptions. In this
     * operation of the demonstration client, we only allow one subscription.
     * This should be returned as the active subscription.
     */
    @Test
    void testRemoveClientSubscription() {
        // Mock the internal class - no other subscription exists
        doReturn(this.secomClient).when(this.secomService).getClient(any());
        doReturn(Optional.of(this.removeSubscriptionResponseObject)).when(this.secomClient).removeSubscription(any());
        doReturn(Optional.of(this.subscription)).when(this.subscriptionService).getActiveSubscription();

        // Perform the service call
        RemoveSubscriptionResponseObject result = this.subscriptionService.removeClientSubscription("urn:mrn:service");

        // Make sure the result seems OK
        assertNotNull(result);
        assertEquals(this.removeSubscriptionResponseObject.getMessage(), result.getMessage());
    }

    /**
     * Test that we can successfully remove existing subscriptions. In this
     * operation of the demonstration client, we only allow one subscription.
     * If no active subscription exists, an error should be thrown. We don't
     * bother too much at this point with this error.
     */
    @Test
    void testRemoveClientSubscriptionNoExisting() {
        // Mock the internal class - no other subscription exists
        doReturn(Optional.empty()).when(this.subscriptionService).getActiveSubscription();

        // Perform the service call
        assertThrows(RuntimeException.class, () -> this.subscriptionService.removeClientSubscription("urn:mrn:service")
        );
    }

}