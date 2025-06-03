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

import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIROperationDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common.FHIRR4SpecUtils;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.ElementExpansionType;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the FHIR R4 resource definition.
 */
public class FHIRR4ResourceDef implements FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> {

    private final Map<String, FHIRSearchParamDef> searchParamDefs = new HashMap<>();
    private final Map<String, FHIROperationDef> operationDefMap = new HashMap<>();
    private StructureDefinition definition;
    private DefKind kind;
    private FHIRR4ResourceDef parentResource;

    @Override
    public StructureDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(StructureDefinition definition) {
        this.definition = definition;
    }

    @Override
    public List<Extension> getExtensions() {
        return definition.getExtension();
    }

    @Override
    public Map<String, FHIRSearchParamDef> getSearchParameters() {
        return searchParamDefs;
    }

    @Override
    public Map<String, FHIROperationDef> getOperations() {
        return operationDefMap;
    }

    @Override
    public void addSearchParam(String url, FHIRSearchParamDef searchParam) {
        searchParamDefs.put(url, searchParam);
    }

    @Override
    public FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> parentResource) {
        this.parentResource = (FHIRR4ResourceDef) parentResource;
    }

    @Override
    public StructureDefinition getDataType(String fhirPath) {
        return null;
    }

    /**
     * Returns elements of the FHIR resource, given the expansion type: snapshot|differential.
     *
     * @param expansionType values: snapshot|differential
     * @return elements of the FHIR resource
     */
    @Override
    public List<ElementDefinition> getElements(ElementExpansionType expansionType) {
        List<ElementDefinition> elementDefinitions = null;
        List<ElementDefinition> processableElementDefinitions = null;

        if (expansionType.equals(ElementExpansionType.SNAPSHOT)) {
            elementDefinitions = definition.getSnapshot().getElement();
            processableElementDefinitions = new ArrayList<>();
        } else if (expansionType.equals(ElementExpansionType.DIFFERENTIAL)) {
            elementDefinitions = definition.getDifferential().getElement();
            processableElementDefinitions = new ArrayList<>();
        }

        if (elementDefinitions != null) {
            for (ElementDefinition elementDefinition : elementDefinitions) {
                if (FHIRR4SpecUtils.canSkip(definition, elementDefinition)) {
                    continue;
                }
                processableElementDefinitions.add(elementDefinition);
            }
        }
        return processableElementDefinitions;
    }

    public String getDefinitionType() {
        return this.definition.getKind().toCode();
    }

    /**
     * Returns resource type kind. values("resource", "logical", "invalid").
     *
     * @return Resource type kind
     */
    public DefKind getKind() {
        return kind;
    }

    public void setKind(DefKind kind) {
        this.kind = kind;
    }
}
