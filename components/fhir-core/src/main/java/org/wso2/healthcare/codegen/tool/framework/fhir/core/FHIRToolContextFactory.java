/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org).
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

package org.wso2.healthcare.codegen.tool.framework.fhir.core;

import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.FHIRR4ToolContext;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common.FHIRR4SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.FHIRR5ToolContext;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common.FHIRR5SpecificationData;

public class FHIRToolContextFactory {
    public static AbstractFHIRToolContext getToolContext(String fhirVersion) {
        switch (fhirVersion.toLowerCase()) {
            case "r4": {
                FHIRR4ToolContext fhirR4ToolContext = new FHIRR4ToolContext();
                fhirR4ToolContext.setSpecificationData(FHIRR4SpecificationData.getDataHolderInstance());
                return fhirR4ToolContext;
            }
            case "r5": {
                FHIRR5ToolContext fhirr5ToolContext = new FHIRR5ToolContext();
                fhirr5ToolContext.setSpecificationData(FHIRR5SpecificationData.getDataHolderInstance());
                return fhirr5ToolContext;
            }
            default: {
                throw new IllegalArgumentException("Unsupported FHIR version. Only R4 and R5 are supported.");
            }
        }
    }
}
