/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.hl7.fhir.r4.model.Resource;
import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;

public class FHIRTerminologyDef implements SpecModel {

    private String url;
    private Resource terminologyResource;

    public Resource getTerminologyResource() {
        return terminologyResource;
    }

    public void setTerminologyResource(Resource terminologyResource) {
        this.terminologyResource = terminologyResource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
