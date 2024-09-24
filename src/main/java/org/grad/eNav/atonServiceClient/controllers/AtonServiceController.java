package org.grad.eNav.atonServiceClient.controllers;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.grad.secom.core.models.ResponseSearchObject;
import org.grad.secom.core.models.SearchFilterObject;
import org.grad.secom.core.models.SearchObjectResult;
import org.grad.secom.core.models.SearchParameters;
import org.grad.secom.springboot3.components.SecomClient;
import org.grad.secom.springboot3.components.SecomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;

import static java.util.function.Predicate.not;

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
     * The SECOM Service URL to subscribe to.
     */
    @Value("${service.variable.mcp.service-registry.endpoint:}")
    private String secomServiceRegistryUrl;

    /**
     * The SECOM Configuration properties.
     */
    @Autowired
    SecomConfigProperties secomConfigProperties;

    // Class Variables
    SecomClient secomClient;

    /**
     * The initialisation operation of the controller in order to setup a
     * SECOM client that can respond with acknowledgements.
     */
    @PostConstruct
    void init() throws IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, URISyntaxException {
        if(Strings.isNotBlank(secomServiceRegistryUrl)) {
            this.secomClient = new SecomClient(
                    new URI(secomServiceRegistryUrl).toURL(),
                    this.secomConfigProperties
            );
        }
    }

    /**
     * POST /api/aton-services: Retrieves a list of all suitable aton-services
     * based on S-125 that have been registered into the asspociated MCP
     * Service Registry.
     *
     * @param pageable the paging information for the request
     * @return A list of all MCP Service Registry resutls
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchObjectResult> getAtonServices(Pageable pageable) {
        // Create a search filter object
        final SearchFilterObject searchFilterObject = new SearchFilterObject();
        final SearchParameters searchParameters = new SearchParameters();
        searchParameters.setKeywords("s-125");
        searchFilterObject.setQuery(searchParameters);
        // Return the retrieved list
        return this.secomClient.searchService(
                    searchFilterObject,
                    pageable.getPageNumber(),
                    pageable.getPageSize())
                .map(ResponseSearchObject::getSearchServiceResult)
                .orElse(Collections.emptyList())
                .stream()
                .filter(not(result -> result.getName().contains("client")))
                .toList();
    }

}
