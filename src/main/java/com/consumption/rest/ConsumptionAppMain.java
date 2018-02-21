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
/**
 * 
 */
package com.consumption.rest;

import org.jsondoc.spring.boot.starter.EnableJSONDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Sinisa Kovacevic 23-01-2018. -- Initial implementation 
 *
 */
@EnableJSONDoc
@ComponentScan
@SpringBootApplication
public class ConsumptionAppMain {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumptionAppMain.class);
    
    public static void main(String[] args) {
        SpringApplication.run(ConsumptionAppMain.class);
        LOGGER.info("Consumption Application Initialized!");
    }
}


