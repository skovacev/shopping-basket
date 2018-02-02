/*-
 * #%L
 * Shopping Basket
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
package com.typeqast.job.rest.pojos;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "IndexData", description = "Basic information about project name and version.")
public class IndexData {

    @ApiObjectField(name = "name", description = "Shopping Basket Application", required = true, order = 1)
    private final String name = "Shopping Basket App";

    @ApiObjectField(name = "version", description = "Shopping Basket Application version", required = true, order = 1)
    private final String version = "0.0.1-SNAPSHOT";

    public IndexData() {}

    /**
     * Get Application name.
     * 
     * @return name of IoT service.
     */
    public String getName() {
        return name;
    }

    /**
     * Get Application version
     * 
     * @return version of IoT service.
     */
    public String getVersion() {
        return version;
    }
}
