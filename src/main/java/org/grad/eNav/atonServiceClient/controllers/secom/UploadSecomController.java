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
import _int.iala_aism.s125.gml._0_0.S125AidsToNavigationType;
import _net.opengis.gml.profiles.AbstractFeatureMemberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.grad.eNav.atonServiceClient.utils.GeometryJSONDeserializer;
import org.grad.eNav.atonServiceClient.utils.GeometryJSONSerializer;
import org.grad.eNav.atonServiceClient.utils.GeometryS125Converter;
import org.grad.eNav.s125.utils.S125Utils;
import org.grad.secom.core.interfaces.UploadSecomInterface;
import org.grad.secom.core.models.UploadObject;
import org.grad.secom.core.models.UploadResponseObject;
import org.grad.secom.core.models.enums.SECOM_ResponseCodeEnum;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Component
@Path("/")
@Validated
@Slf4j
public class UploadSecomController implements UploadSecomInterface {

    /**
     * The Object Mapper.
     */
    @Autowired
    ObjectMapper objectMapper;

    /**
     * Attach the web-socket as a simple messaging template
     */
    @Autowired
    SimpMessagingTemplate webSocket;

    /**
     * POST /api/secom/v1/dataset : Accepts the incoming AtoN Service data in
     * a SECOM-compliant S-125 format.
     *
     * @param uploadObject the upload object
     * @return the upload response object
     */
    public UploadResponseObject upload(@Valid UploadObject uploadObject) {
        // Initialise the response
        UploadResponseObject uploadResponseObject = new UploadResponseObject();

        // Get the upload object data
        String encoded =  uploadObject.getEnvelope().getData();
        String decoded = new String(Base64.decode(encoded));
        
        // Decode the and down the web-socket
        try {
            S125Utils.unmarshallS125(decoded)
                    .getImembersAndMembers()
                    .stream()
                    .filter(MemberType.class::isInstance)
                    .map(MemberType.class::cast)
                    .map(MemberType::getAbstractFeature)
                    .map(JAXBElement::getValue)
                    .filter(S125AidsToNavigationType.class::isInstance)
                    .map(S125AidsToNavigationType.class::cast)
                    .forEach(s125PubSubData -> this.webSocket.convertAndSend("/topic/secom/subscription/update" , s125PubSubData));
        } catch (JAXBException e) {
            uploadResponseObject.setSECOM_ResponseCode(SECOM_ResponseCodeEnum.SCHEMA_VALIDATION_ERROR);
            uploadResponseObject.setResponseText("Unable to validate the provided S-125 XML schema.");
        }

        // Return the response
        return uploadResponseObject;
    }

}
