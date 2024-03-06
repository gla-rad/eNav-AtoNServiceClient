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

package org.grad.eNav.atonServiceClient.models.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.UUID;

/**
 * The Subscription Class.
 * <p/>
 * This is a simple class to hold the subscription information.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Entity
public class Subscription implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_generator")
    @SequenceGenerator(name="subscription_generator", sequenceName = "subscription_generator_seq", allocationSize=1)
    @Column(name = "id", nullable = false, precision = 24, scale = 0)
    private BigInteger id;

    @Column(unique = true)
    private String mrn;

    private UUID identifier;

    /**
     * An empty constructor.
     */
    public Subscription() {
    }

    /**
     * A constuctor using the MRN.
     *
     * @param mrn the subscription MRN
     */
    public Subscription(String mrn) {
        this.mrn = mrn;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Gets mrn.
     *
     * @return the mrn
     */
    public String getMrn() {
        return mrn;
    }

    /**
     * Sets mrn.
     *
     * @param mrn the mrn
     */
    public void setMrn(String mrn) {
        this.mrn = mrn;
    }

    /**
     * Gets identifier.
     *
     * @return the identifier
     */
    public UUID getIdentifier() {
        return identifier;
    }

    /**
     * Sets identifier.
     *
     * @param identifier the identifier
     */
    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }
}
