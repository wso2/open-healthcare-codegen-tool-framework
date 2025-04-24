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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4;

import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.core.SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.FHIRToolContext;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common.FHIRR4SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.FHIRToolConfig;

/**
 * Context for FHIR tools.
 */
public class FHIRR4ToolContext extends FHIRToolContext {

    private FHIRToolConfig config;
    private FHIRSpecificationData specificationData;

    @Override
    protected void setConfig(ToolConfig toolConfig) {
        this.config = (FHIRToolConfig) toolConfig;
    }

    @Override
    public FHIRToolConfig getConfig() {
        return config;
    }

    @Override
    public FHIRSpecificationData getSpecificationData() {
        return specificationData;
    }

    @Override
    public void setSpecificationData(SpecificationData specificationData) {
        this.specificationData = (FHIRR4SpecificationData) specificationData;
    }
}
