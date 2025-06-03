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

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.ElementExpansionType;

import java.util.List;
import java.util.Map;

/**
 * This is the generic class for all FHIR resource definitions irrespective of FHIR version
 * It is parameterized with the following types:
 *
 * @StructureDefinition - import StructureDefinition related to a FHIR version
 * @Extension - import Extension related to a FHIR version
 * @ElementDefinition - import ElementDefinition related to a FHIR version
 */

public interface FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> extends SpecModel {

    StructureDefinition getDefinition();

    void setDefinition(StructureDefinition definition);

    List<Extension> getExtensions();

    Map<String, FHIRSearchParamDef> getSearchParameters();

    Map<String, FHIROperationDef> getOperations();

    void addSearchParam(String url, FHIRSearchParamDef searchParam);

    FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> getParentResource();

    void setParentResource(FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> parentResource);

    StructureDefinition getDataType(String fhirPath);

    List<ElementDefinition> getElements(ElementExpansionType expansionType);

    String getDefinitionType();

    /**
     * Returns resource type kind. values("resource", "logical", "invalid").
     *
     * @return Resource type kind
     */
    DefKind getKind();

    void setKind(DefKind kind);
}
