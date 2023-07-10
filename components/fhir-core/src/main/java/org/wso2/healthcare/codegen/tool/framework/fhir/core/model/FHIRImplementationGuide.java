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

import java.util.HashMap;
import java.util.Map;

public class FHIRImplementationGuide {
    private String name;
    private Map<String, FHIRResourceDef> resources;
    private Map<String, FHIRSearchParamDef> searchParameters;
    private Map<String, FHIROperationDef> operations;

    public FHIRImplementationGuide() {
        resources = new HashMap<>();
        searchParameters = new HashMap<>();
        operations = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, FHIRResourceDef> getResources() {
        return resources;
    }

    public void setResources(Map<String, FHIRResourceDef> resources) {
        this.resources = resources;
    }

    public Map<String, FHIRSearchParamDef> getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(Map<String, FHIRSearchParamDef> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public Map<String, FHIROperationDef> getOperations() {
        return operations;
    }

    public void setOperations(Map<String, FHIROperationDef> operations) {
        this.operations = operations;
    }
}
