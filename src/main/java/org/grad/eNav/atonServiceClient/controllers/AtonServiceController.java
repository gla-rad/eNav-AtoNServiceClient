package org.grad.eNav.atonServiceClient.controllers;

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.services.SecomService;
import org.grad.secom.core.models.SearchObjectResult;
import org.grad.secom.core.models.SummaryObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * The SECOM Service.
     */
    @Autowired
    SecomService secomService;

    /**
     * POST /api/aton_service: Retrieves a list of all suitable aton-services
     * based on S-125 that have been registered into the associated MCP
     * Service Registry.
     *
     * @param pageable the paging information for the request
     * @return A list of all MCP Service Registry resutls
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchObjectResult> getAtonServices(Pageable pageable) {
        return this.secomService.getAtonServices(pageable);
    }

    /**
     * POST /api/aton_service/{mrn}/summary: Retrieves a list of all available
     * S-125 datasets for the S-125 AtoN service defined by the provided MRN.
     *
     * @param mrn  the MRN of the service to retrieve the datasets for
     * @param pageable the paging information for the request
     * @return A list of all MCP Service Registry resutls
     */
    @GetMapping(value="/{mrn}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SummaryObject> getAtonServices(@PathVariable("mrn") String mrn, Pageable pageable) {
        return this.secomService.getAtonDatasets(mrn, pageable);
    }

}
