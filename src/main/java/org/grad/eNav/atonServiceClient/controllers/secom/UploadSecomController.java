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

import _int.iala_aism.s125.gml._0_0.MemberType;
import _int.iala_aism.s125.gml._0_0.AidsToNavigationType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.grad.eNav.s125.utils.S125Utils;
import org.grad.secom.core.interfaces.UploadSecomInterface;
import org.grad.secom.core.models.*;
import org.grad.secom.core.models.enums.AckRequestEnum;
import org.grad.secom.core.models.enums.AckTypeEnum;
import org.grad.secom.core.models.enums.SECOM_ResponseCodeEnum;
import org.grad.secom.springboot3.components.SecomClient;
import org.grad.secom.springboot3.components.SecomConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
@Path("/")
@Validated
@Slf4j
public class UploadSecomController implements UploadSecomInterface {

    /**
     * The SECOM Service URL to subscribe to.
     */
    @Value("${gla.rad.aton-service-client.secom.serviceIUrl:}")
    private String secomServiceUrl;

    /**
     * Attach the web-socket as a simple messaging template
     */
    @Autowired
    SimpMessagingTemplate webSocket;

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
    void init() throws IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        if(Strings.isNotBlank(this.secomServiceUrl)) {
            this.secomClient = new SecomClient(
                    new URL(this.secomServiceUrl),
                    this.secomConfigProperties
            );
        }
    }

    /**
     * POST /api/secom/v1/object : Accepts the incoming AtoN Service data in
     * a SECOM-compliant S-125 format.
     *
     * @param uploadObject the upload object
     * @return the upload response object
     */
    @Tag(name = "SECOM")
    public UploadResponseObject upload(@Valid UploadObject uploadObject) {
        // Initialise the response
        UploadResponseObject uploadResponseObject = new UploadResponseObject();

        // Get the upload object data
        String data = new String(uploadObject.getEnvelope().getData(), StandardCharsets.UTF_8);
        
        // Decode the data and down the web-socket
        try {
            S125Utils.getS125Members(data)
                    .stream()
                    .filter(AidsToNavigationType.class::isInstance)
                    .map(AidsToNavigationType.class::cast)
                    .forEach(s125PubSubData -> this.webSocket.convertAndSend("/topic/secom/subscription/update" , s125PubSubData));

            // Now generate an acknowledgement to be sent back if required
            if(this.secomClient != null) {
                Optional.of(uploadObject)
                        .map(UploadObject::getEnvelope)
                        .map(EnvelopeUploadObject::getAckRequest)
                        .filter(not(AckRequestEnum.NO_ACK_REQUESTED::equals))
                        .ifPresent(ackType -> {
                            final AcknowledgementObject acknowledgementObject = new AcknowledgementObject();
                            final EnvelopeAckObject envelopeAckObject = new EnvelopeAckObject();
                            envelopeAckObject.setCreatedAt(LocalDateTime.now());
                            envelopeAckObject.setTransactionIdentifier(uploadObject.getEnvelope().getTransactionIdentifier());
                            envelopeAckObject.setAckType(AckTypeEnum.DELIVERED_ACK);
                            acknowledgementObject.setEnvelope(envelopeAckObject);
                            this.secomClient.acknowledgment(acknowledgementObject);
                        });
            }
        } catch (JAXBException ex) {
            uploadResponseObject.setSECOM_ResponseCode(SECOM_ResponseCodeEnum.SCHEMA_VALIDATION_ERROR);
            uploadResponseObject.setResponseText("Unable to validate the provided S-125 XML schema.");
        }

        // Return the response
        return uploadResponseObject;
    }

}
