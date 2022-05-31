package org.grad.eNav.atonServiceClient.controllers;

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.models.dto.DtoObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * The DTO Object Controller.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/object")
@Slf4j
public class DtoObjectController {


    /**
     * GET /api/object : Returns a DTO Object.
     *
     * @return the DTO object
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DtoObject> date() {
        DtoObject dtoObject = new DtoObject();
        dtoObject.setDatetime(LocalDateTime.now());
        return ResponseEntity.ok()
                .body(dtoObject);
    }

}
