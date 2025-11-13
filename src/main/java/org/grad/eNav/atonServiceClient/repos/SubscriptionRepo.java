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

package org.grad.eNav.atonServiceClient.repos;

import org.grad.eNav.atonServiceClient.models.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the Subscription entities.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface SubscriptionRepo extends JpaRepository<Subscription, BigInteger> {

    /**
     * Retrieves all the subscription that match a specific UUID identifier.
     *
     * @param identifier The UUID identifier to match the subscription for
     * @return the subscription that matches the specified UUID identifier
     */
    Optional<Subscription> findByIdentifier(UUID identifier);

    /**
     * Retrieves all the subscription that match a specific MRN
     *
     * @param mrn The MRN to match the subscription for
     * @return the subscription that matches the specified MRN
     */
    Optional<Subscription> findByMrn(String mrn);

}
