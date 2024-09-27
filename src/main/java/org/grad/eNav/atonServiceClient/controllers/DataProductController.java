package org.grad.eNav.atonServiceClient.controllers;

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonServiceClient.services.SecomService;
import org.grad.secom.core.models.SearchObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The REST Controller for managing Data Product related operations.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@RestController
@RequestMapping("/api/data_product")
@Slf4j
public class DataProductController {

    /**
     * The SECOM Service.
     */
    @Autowired
    SecomService secomService;

    /**
     * GET /api/registered_service/{keyword}: Retrieves a list of all suitable aton-services
     * based on S-125 that have been registered into the associated MCP
     * Service Registry.
     *
     * @param pageable the paging information for the request
     * @return A list of all MCP Service Registry resutls
     */
    @GetMapping(value="/{dataProductId}/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchObjectResult> getRegisteredServices(@PathVariable String dataProductId, Pageable pageable) {
        return this.secomService.getRegisteredServices(dataProductId, pageable);
    }

}
