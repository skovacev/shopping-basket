/*-
 * #%L
 * Consumption App
 * %%
 * Copyright (C) 2015 - 2018 Pivotal Software, Inc.
 * %%
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
 * #L%
 */
package com.consumption.rest.controllers;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVerb;
import org.jsondoc.core.pojo.ApiVisibility;
import org.jsondoc.spring.boot.starter.EnableJSONDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.consumption.rest.exceptions.ResponseError;
import com.consumption.rest.pojos.IndexData;
import com.consumption.rest.utils.BaseClass;

@Api(name = "Consumption App Index services",
        description = "Index Service",
        visibility = ApiVisibility.PUBLIC,
        stage = ApiStage.RC)
@ApiVersion(since = "1", until = "2")
@ApiAuthNone
@EnableJSONDoc
@RestController
@RequestMapping("/api/v0")
public class IndexController extends BaseClass implements ErrorController {
    
    private Logger logger = LoggerFactory.getLogger(BaseClass.class);
    
    /**
     * Get logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    @ApiMethod(path = "/",
            verb = ApiVerb.GET,
            description = "Get root page with basic info.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get root page with basic info.",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public IndexData error() {
        getLogger().debug("REST " + RequestMethod.GET + " call to: /api/v0/");
        return new IndexData();
    }

    @ApiMethod(path = "/error",
            verb = ApiVerb.GET,
            description = "Get error page.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get error page.",
            responsestatuscode = "500 - Internal Server Error.")
    @RequestMapping("/error")
    public ResponseEntity<ResponseError> getErrorPathResponse() {
        getLogger().debug("REST " + RequestMethod.GET + " call to: /api/v0/error");
        return new ResponseEntity<ResponseError>(
                new ResponseError("Server error. Check input data and try again.", HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public String getErrorPath() {
        getLogger().warn("Redirect REST call to: /api/v0/error");
        return "/api/v0/error";
    }
}
