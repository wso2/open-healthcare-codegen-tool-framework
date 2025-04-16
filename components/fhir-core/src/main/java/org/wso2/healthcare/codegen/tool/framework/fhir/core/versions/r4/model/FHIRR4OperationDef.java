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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model;

import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.OperationDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIROperationDef;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds FHIR R4 Operation definition model.
 */
public class FHIRR4OperationDef implements FHIROperationDef<OperationDefinition> {

    private OperationDefinition operationDefinition;
    private List<String> targetResources;

    @Override
    public OperationDefinition getOperationDefinition() {
        return operationDefinition;
    }

    @Override
    public void setOperationDefinition(OperationDefinition operationDefinition) {
        this.operationDefinition = operationDefinition;
        populateTargetResources(operationDefinition);
    }

    @Override
    public void populateTargetResources(OperationDefinition operationDefinition) {
        List<String> resources = new ArrayList<>();
        for (CodeType resource : operationDefinition.getResource()) {
            resources.add(resource.getCode());
        }
        this.targetResources = resources;
    }

    @Override
    public List<String> getTargetResources() {
        return targetResources;
    }
}
