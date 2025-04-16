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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.HashSet;
import java.util.Set;

/**
 * This class holds the data model for API definition.
 */
public class APIDefinition {

    private OpenAPI openAPI;
    private String resourceType;
    private Set<String> supportedProfiles;
    private Set<String> supportedIgs;

    public APIDefinition() {
        this.openAPI = new OpenAPI();
        this.supportedProfiles = new HashSet<>();
        this.supportedIgs = new HashSet<>();
    }

    public APIDefinition(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    public OpenAPI getOpenAPI() {
        return openAPI;
    }

    public void setOpenAPI(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Set<String> getSupportedProfiles() {
        return supportedProfiles;
    }

    public void addSupportedProfile(String profile) {
        this.supportedProfiles.add(profile);
    }

    public void setSupportedProfiles(Set<String> supportedProfiles) {
        this.supportedProfiles = supportedProfiles;
    }

    public Set<String> getSupportedIgs() {
        return supportedIgs;
    }

    public void setSupportedIgs(Set<String> supportedIgs) {
        this.supportedIgs = supportedIgs;
    }

    public void addSupportedIg(String igName) {
        this.supportedIgs.add(igName);
    }
}
