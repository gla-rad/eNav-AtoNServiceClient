package org.grad.eNav.atonServiceClient.controllers;

import lombok.extern.slf4j.Slf4j;
import org.grad.secom.models.SubscriptionRequestObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/subscription")
@Validated
@Slf4j
public class SubscriptionController {

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
