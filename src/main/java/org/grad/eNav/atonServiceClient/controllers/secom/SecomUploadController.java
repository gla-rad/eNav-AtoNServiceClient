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

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.grad.secom.interfaces.UploadInterface;
import org.grad.secom.models.UploadObject;
import org.grad.secom.models.UploadResponseObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/secom")
@Validated
@Slf4j
public class SecomUploadController implements UploadInterface {


    /**
     * POST /api/secom/v1/dataset : Accepts the incoming AtoN Service data in
     * a SECOM-compliant S-125 format.
     *
     * @param uploadObject the upload object
     * @return the upload response object
     */
    @Override
    @Tag(name = "SECOM")
    @PostMapping(value = UPLOAD_INTERFACE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadResponseObject> upload(@Valid UploadObject uploadObject) {
        return ResponseEntity.ok().build();
    }

}
