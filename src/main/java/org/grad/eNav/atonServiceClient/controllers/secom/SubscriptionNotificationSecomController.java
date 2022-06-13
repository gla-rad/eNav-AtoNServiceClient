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

import lombok.extern.slf4j.Slf4j;
import org.grad.secom.interfaces.jaxrs.SubscriptionNotificationSecomInterface;
import org.grad.secom.models.SubscriptionNotificationObject;
import org.grad.secom.models.SubscriptionNotificationResponseObject;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path("/")
@Validated
@Slf4j
public class SubscriptionNotificationSecomController implements SubscriptionNotificationSecomInterface {

    /**
     * POST /v1/subscription/notification : The interface receives notifications
     * when a subscription is created or removed by the information provider.
     *
     * @param subscriptionNotificationObject the subscription notification request object
     * @return the subscription notification response object
     */
    public SubscriptionNotificationResponseObject subscriptionNotification(@Valid SubscriptionNotificationObject subscriptionNotificationObject) {
        return null;
    }

    @Path(SUBSCRIPTION_NOTIFICATION_INTERFACE_PATH)
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String subscriptionNotificationGet() {
        return "test";
    }

}
