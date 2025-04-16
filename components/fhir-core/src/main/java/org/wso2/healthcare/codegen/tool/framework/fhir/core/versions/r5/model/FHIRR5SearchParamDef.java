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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model;

import org.hl7.fhir.r5.model.Enumeration;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.SearchParameter;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds FHIR R5 Search Parameter definition model.
 */
public class FHIRR5SearchParamDef implements FHIRSearchParamDef<SearchParameter> {
    private SearchParameter searchParameter;

    public FHIRR5SearchParamDef() {
    }

    public FHIRR5SearchParamDef(SearchParameter searchParameter) {
        this.searchParameter = searchParameter;
    }

    @Override
    public SearchParameter getSearchParameter() {
        return searchParameter;
    }

    @Override
    public void setSearchParameter(SearchParameter searchParameter) {
        this.searchParameter = searchParameter;
    }

    @Override
    public List<String> getBaseResources() {
        List<String> baseResources = new ArrayList<>();
        List<Enumeration<Enumerations.VersionIndependentResourceTypesAll>> baseResourceTypes = this.searchParameter.getBase();

        for (Enumeration<Enumerations.VersionIndependentResourceTypesAll> baseResourceType : baseResourceTypes) {
            baseResources.add(baseResourceType.getValue().toCode());
        }

        return baseResources;
    }
}
