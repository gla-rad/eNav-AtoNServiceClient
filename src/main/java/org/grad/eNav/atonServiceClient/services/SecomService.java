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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.grad.eNav.atonServiceClient.models.domain.SignedDatasetContent;
import org.grad.eNav.atonServiceClient.utils.X509Utils;
import org.grad.secom.core.exceptions.SecomNotFoundException;
import org.grad.secom.core.exceptions.SecomValidationException;
import org.grad.secom.core.models.*;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.grad.secom.springboot3.components.SecomClient;
import org.grad.secom.springboot3.components.SecomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.function.Predicate.not;

/**
 * The SECOM Service Class
 * <p/>
 * This class implements all the basic SECOM operations. In the case fot he
 * AtoN Service Client these are mainly related to the subscription operation
 * for the S-125 AtoN data.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class SecomService {

    /**
     * The maximum number of results for unpaged discovery searches
     */
    public static int MAX_UNPAGED_RESULTS_NO = 100;

    /**
     * The Service Registry URL.
     */
    @Value("${secom.service-registry.url:}" )
    String discoveryServiceUrl;

    /**
     * The SECOM Configuration Properties.
     */
    @Autowired
    SecomConfigProperties secomConfigProperties;

    // Class Variables
    SecomClient discoveryService;
    private SearchObjectResult searchObjectResult;
    private ResponseSearchObject responseSearchObject;

    /**
     * The service post-construct operations where the handler auto-registers
     * it-self to the S-125 publication channel.
     */
    @PostConstruct
    public void init() {
        log.info("SECOM Service is booting up...");
        this.discoveryService = Optional.ofNullable(this.discoveryServiceUrl)
                .filter(StringUtils::isNotBlank)
                .map(url -> {
                    try {
                        return URI.create(url).toURL();
                    } catch (MalformedURLException ex) {
                        log.error("Invalid SECOM discovery service URL provided...", ex);
                        return null;
                    }
                })
                .map(url -> {
                    try {
                        return new SecomClient(url, this.secomConfigProperties);
                    } catch (IOException | KeyStoreException |
                             NoSuchAlgorithmException | CertificateException |
                             UnrecoverableKeyException ex) {
                        log.error("Unable to initialise the SSL context for the SECOM discovery service...", ex);
                        return null;
                    }
                })
                .orElse(null);
    }

    /**
     * When shutting down the application we need to make sure that all
     * threads have been gracefully shutdown as well.
     */
    @PreDestroy
    public void destroy() {
        log.info("SECOM Service is shutting down...");
        this.discoveryService = null;
    }

    /**
     * Based on an MRN provided, this function will contact the SECOM discovery
     * service (in this case it's the MCP MSR) and request the client endpoint
     * URI. It will then construct a SECOM client to be returned for the URI
     * discovered.
     *
     * @param mrn the MRN to be lookup up
     * @return the SECOM client for the endpoint matching the provided URI
     */
    public SecomClient getClient(String mrn) {
        // Validate the MRN
        Optional.ofNullable(mrn)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new SecomValidationException("Cannot request a service discovery for an empty/invalid MRN"));

        // Make sure the service registry is available
        Optional.ofNullable(this.discoveryService)
                .filter(Objects::nonNull)
                .orElseThrow(() -> new SecomValidationException("Subscription request found for S-125 dataset updates but no connection to service registry"));

        // Create the discovery service search filter object for the provided MRN
        final SearchFilterObject searchFilterObject = new SearchFilterObject();
        final SearchParameters searchParameters = new SearchParameters();
        searchParameters.setInstanceId(mrn);
        searchFilterObject.setQuery(searchParameters);

        // Lookup the endpoints of the clients from the SECOM discovery service
        final List<SearchObjectResult> instances = Optional.ofNullable(this.discoveryService)
                .flatMap(ds -> ds.searchService(searchFilterObject, 0, Integer.MAX_VALUE))
                .map(ResponseSearchObject::getSearchServiceResult)
                .orElse(Collections.emptyList());

        // Extract the latest matching instance
        final SearchObjectResult instance = instances.stream()
                .max(Comparator.comparing(SearchObjectResult::getVersion))
                .orElseThrow(() -> new SecomNotFoundException(mrn));

        // Now construct and return a SECOM client for the discovered URI
        try {
            return new SecomClient(URI.create(instance.getEndpointUri()).toURL(), this.secomConfigProperties);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new SecomValidationException(ex.getMessage());
        }
    }

    /**
     * Searches the connected discovery client to identify all the registered
     * services that seems to provide AtoN information encoded in S-100. It
     * supports a paged search and the complete SECOM search result will be
     * returned.
     *
     * @param pageable the paging information for the search
     * @return all the matching S-125 AtoN services currently registered
     */
    public List<SearchObjectResult> getRegisteredServices(@NotNull String keyword, @NotNull Pageable pageable) {
        // Create a search filter object
        final SearchFilterObject searchFilterObject = new SearchFilterObject();
        final SearchParameters searchParameters = new SearchParameters();
        searchParameters.setKeywords(keyword.replace("-","*"));
        searchFilterObject.setQuery(searchParameters);
        // Return the retrieved list
        return this.discoveryService.searchService(
                        searchFilterObject,
                        pageable.isUnpaged()? null : pageable.getPageNumber(),
                        pageable.isUnpaged()? null : pageable.getPageSize())
                .map(ResponseSearchObject::getSearchServiceResult)
                .orElse(Collections.emptyList())
                .stream()
                .filter(not(result -> result.getName().toLowerCase().contains("client")))
                .toList();
    }

    /**
     * For a selected S-125 AtoN service based on its MRN this function will
     * return the list of available datasets using the summary SECOM interface.
     * Note that this function will return the actual SECOM summary objects
     * list.
     *
     * @param mrn the MRN of the service to get the list of datasets for
     * @param pageable the paging information for the search
     * @return the list of the dataset summary information
     */
    public List<SummaryObject> getServiceDatasets(@NotNull String mrn,
                                                  @NotNull Pageable pageable) {
        // Access the SECOM client based on the MRN
        SecomClient secomClient = this.getClient(mrn);

        // Request the available datasets using the summary interface
        return secomClient.getSummary(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                pageable.isUnpaged()? null : pageable.getPageNumber(),
                pageable.isUnpaged()? null : pageable.getPageSize())
                .map(GetSummaryResponseObject::getSummaryObject)
                .orElse(Collections.emptyList());
    }

    /**
     * For a selected S-125 AtoN service based on its MRN this function will
     * return the content of the selected datasets using the Get SECOM
     * interface. Note that this function will return the actual SECOM data
     * response objects list/
     *
     * @param mrn the MRN of the service to get the list of dataset content for
     * @param dataReference the object data reference
     * @param dataProductType the object data product type
     * @param productVersion the object data product version
     * @param geometry the object geometry
     * @param unlocode the object UNLOCODE
     * @param validFrom the object valid from time
     * @param validTo the object valid to time
     * @param pageable the paging information for the action
     * @return
     */
    public List<SignedDatasetContent> getServiceDatasetContent(@NotNull String mrn,
                                                               String dataReference,
                                                               SECOM_DataProductType dataProductType,
                                                               String productVersion,
                                                               String geometry,
                                                               String unlocode,
                                                               LocalDateTime validFrom,
                                                               LocalDateTime validTo,
                                                               @NotNull Pageable pageable) {
        // Access the SECOM client based on the MRN
        SecomClient secomClient = this.getClient(mrn);

        // Request the available dataset contents using the summary interface
        return secomClient.get(
                        Optional.ofNullable(dataReference).map(UUID::fromString).orElse(null),
                        ContainerTypeEnum.S100_DataSet,
                        dataProductType,
                        productVersion,
                        geometry,
                        unlocode,
                        validFrom,
                        validTo,
                        pageable.isUnpaged() ? null : pageable.getPageNumber(),
                        pageable.isUnpaged() ? null : pageable.getPageSize())
                .map(GetResponseObject::getDataResponseObject)
                .orElse(Collections.emptyList())
                .stream()
                .map(dataResponseObject -> {
                    final SignedDatasetContent signedDatasetContent = new SignedDatasetContent();
                    
                    Optional.of(dataResponseObject)
                            .map(DataResponseObject::getExchangeMetadata)
                            .map(SECOM_ExchangeMetadataObject::getDigitalSignatureValue)
                            .map(DigitalSignatureValue::getPublicCertificate)
                            .map(X509Utils::extractFromCertificatePem)
                            .map(X509Utils::extractUIDFromCertificate)
                            .ifPresent(signedDatasetContent::setSignedBy);
                    Optional.of(dataResponseObject)
                            .map(DataResponseObject::getExchangeMetadata)
                            .map(SECOM_ExchangeMetadataObject::getDigitalSignatureValue)
                            .map(DigitalSignatureValue::getPublicCertificate)
                            .map(X509Utils::extractFromCertificatePem)
                            .map(X509Utils::extractIssuerUIDFromCertificate)
                            .ifPresent(signedDatasetContent::setIssuedBy);
                    signedDatasetContent.setContent(dataResponseObject.getData());
                    return signedDatasetContent;
                })
                .toList();

    }
}
