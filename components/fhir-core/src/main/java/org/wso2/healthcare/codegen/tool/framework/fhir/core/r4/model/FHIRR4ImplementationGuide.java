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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.model;

import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.oas.model.R4APIDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds data model for FHIR implementation guide related information.
 */
public class FHIRR4ImplementationGuide extends FHIRImplementationGuide {
    private String name;
    private String id;
    private Map<String, FHIRResourceDef> resources;
    private Map<String, FHIRSearchParamDef> searchParameters;
    private Map<String, FHIRR4OperationDef> operations;
    private Map<String, R4APIDefinition> apiDefinitions;

    public FHIRR4ImplementationGuide() {
        resources = new HashMap<>();
        searchParameters = new HashMap<>();
        operations = new HashMap<>();
        apiDefinitions = new HashMap<>();
    }

    @Override
    public Map<String, R4APIDefinition> getApiDefinitions() {
        return apiDefinitions;
    }

    public void addApiDefinition(String key, R4APIDefinition apiDefinition) {
        if (apiDefinitions.containsKey(key)) {
            R4APIDefinition currentApiDef = apiDefinitions.get(key);
            currentApiDef.getOpenAPI().getComponents().getSchemas().putAll(
                    apiDefinition.getOpenAPI().getComponents().getSchemas());
        } else {
            apiDefinitions.put(key, apiDefinition);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<String, FHIRResourceDef> getResources() {
        return resources;
    }

    public void setResources(Map<String, FHIRResourceDef> resources) {
        this.resources = resources;
    }

    @Override
    public Map<String, FHIRSearchParamDef> getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(Map<String, FHIRSearchParamDef> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public Map<String, FHIRR4OperationDef> getOperations() {
        return operations;
    }

    public void setOperations(Map<String, FHIRR4OperationDef> operations) {
        this.operations = operations;
    }
}
