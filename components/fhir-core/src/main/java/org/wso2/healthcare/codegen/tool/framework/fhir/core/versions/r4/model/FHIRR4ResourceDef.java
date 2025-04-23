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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model;

import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common.FHIRR4SpecUtils;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.util.R4DefKind;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.util.R4ElementExpansionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds the FHIR resource definition.
 */
public class FHIRR4ResourceDef extends FHIRResourceDef {

    private StructureDefinition definition;
    private R4DefKind kind;
    private final Map<String, FHIRR4SearchParamDef> searchParamDefs = new HashMap<>();
    private final Map<String, FHIRR4OperationDef> operationDefMap = new HashMap<>();
    private FHIRR4ResourceDef parentResource;

    @Override
    public StructureDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(StructureDefinition definition) {
        this.definition = definition;
    }

    public List<Extension> getExtensions() {
        return definition.getExtension();
    }

    public Map<String, FHIRR4SearchParamDef> getSearchParameters() {
        return searchParamDefs;
    }
    public Map<String, FHIRR4OperationDef> getOperations(){
        return operationDefMap;
    }
    public void addSearchParam(String url, FHIRR4SearchParamDef searchParam){
        searchParamDefs.put(url,searchParam);
    }

    public FHIRR4ResourceDef getParentResource() {
        return parentResource;
    }

    public void setParentResource(FHIRR4ResourceDef parentResource) {
        this.parentResource = parentResource;
    }

    public StructureDefinition getDataType(String fhirPath) {
        return null;
    }

    /**
     * Returns elements of the FHIR resource, given the expansion type: snapshot|differential.
     *
     * @param expansionType values: snapshot|differential
     * @return elements of the FHIR resource
     */
    public List<ElementDefinition> getElements(R4ElementExpansionType expansionType) {
        List<ElementDefinition> elementDefinitions = null;
        List<ElementDefinition> processableElementDefinitions = null;
        if (expansionType.equals(R4ElementExpansionType.SNAPSHOT)) {
            elementDefinitions = definition.getSnapshot().getElement();
            processableElementDefinitions = new ArrayList<>();
        } else if (expansionType.equals(R4ElementExpansionType.DIFFERENTIAL)) {
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
    public R4DefKind getKind() {
        return kind;
    }

    public void setKind(R4DefKind kind) {
        this.kind = kind;
    }
}
