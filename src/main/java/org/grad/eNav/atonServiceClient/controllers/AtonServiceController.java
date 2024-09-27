package org.grad.eNav.atonServiceClient.controllers;

import _int.iho.s125.gml.cs0._1.AidsToNavigationType;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.services.SecomService;
import org.grad.eNav.atonServiceClient.utils.AtonTypeConverter;
import org.grad.secom.core.models.SearchObjectResult;
import org.grad.secom.core.models.SummaryObject;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The REST Controller for managing AtoN Services registered on the MSR.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/aton_service")
@Slf4j
public class AtonServiceController {

    /**
     * Attach the web-socket as a simple messaging template
     */
    @Autowired
    SimpMessagingTemplate webSocket;

    /**
     * The SECOM Service.
     */
    @Autowired
    SecomService secomService;

    /**
     * GET /api/aton_service/{mrn}/summary: Retrieves a list of all available
     * S-125 datasets for the S-125 AtoN service defined by the provided MRN.
     *
     * @param mrn  the MRN of the service to retrieve the datasets for
     * @param pageable the paging information for the request
     * @return A list of all MCP Service Registry resutls
     */
    @GetMapping(value="/{mrn}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SummaryObject> getAtonDatasets(@PathVariable("mrn") String mrn, Pageable pageable) {
        return this.secomService.getAtonDatasets(mrn, pageable);
    }

    /**
     * GET /api/aton_service/{mrn}/data: Retrieves the content of all
     * selected S-125 datasets from the S-125 AtoN service defined by the
     * provided MRN.
     *
     * @param mrn  the MRN of the service to retrieve the datasets for
     * @param dataReference the object data reference
     * @param dataProductType the object data product type
     * @param productVersion the object data product version
     * @param geometry the object geometry
     * @param unlocode the object UNLOCODE
     * @param validFrom the object valid from time
     * @param validTo the object valid to time
     * @param pageable the paging information for the request
     * @return A list of all MCP Service Registry resutls
     */
    @GetMapping(value="/{mrn}/content", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getAtonDatasetContent(@PathVariable("mrn") String mrn,
                                                      UUID dataReference,
                                                      SECOM_DataProductType dataProductType,
                                                      String productVersion,
                                                      String geometry,
                                                      String unlocode,
                                                      LocalDateTime validFrom,
                                                      LocalDateTime validTo,
                                                      Pageable pageable) {
        // Retrieve the S-125 Aton Information and pass it down through the websocket
        this.secomService.getAtonDatasetContent(mrn,
                                                dataReference,
                                                dataProductType,
                                                productVersion,
                                                geometry,
                                                unlocode,
                                                validFrom,
                                                validTo,
                                                pageable)
                .stream()
                .filter(AidsToNavigationType.class::isInstance)
                .map(AidsToNavigationType.class::cast)
                .forEach(aton ->
                        this.webSocket.convertAndSend(
                                "/topic/secom/subscription/update",
                                aton,
                                Collections.singletonMap("aton-type", AtonTypeConverter.convertToSeamarkType(
                                        Arrays.asList(aton.getClass().getInterfaces()).getLast())
                                )
                        )
                );

        // Also send a success response
        return ResponseEntity.ok().build();
    }

}
