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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRDataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4DataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4ImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4SearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4TerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.util.R4DefKind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data holder for parsed FHIR specification data and utilities.
 */
public class FHIRR4SpecificationData extends FHIRSpecificationData {

    private static final FHIRR4SpecificationData DATA_HOLDER_INSTANCE = new FHIRR4SpecificationData();

    private Map<String, FHIRDataTypeDef> dataTypes;
    private Map<String, FHIRImplementationGuide> fhirImplementationGuides;
    private Map<String, Map<String,Coding>> resolvedTerminologies;
    private Map<String, FHIRTerminologyDef> valueSets;
    private Map<String, FHIRTerminologyDef> codeSystems;
    private final HashMap<String, List<FHIRR4SearchParamDef>> internationalSpecSearchParameters = new HashMap<>();

    public static FHIRR4SpecificationData getDataHolderInstance() {
        return DATA_HOLDER_INSTANCE;
    }

    private FHIRR4SpecificationData() {
        dataTypes = new HashMap<>();
        fhirImplementationGuides = new HashMap<>();
        valueSets = new HashMap<>();
        codeSystems = new HashMap<>();
    }

    @Override
    public Map<String, FHIRDataTypeDef> getDataTypes() {
        return dataTypes;
    }

    public void addDataType(String id, FHIRR4DataTypeDef dataTypeDef) {
        dataTypes.putIfAbsent(id, dataTypeDef);
    }

    public void setDataTypes(Map<String, FHIRDataTypeDef> dataTypes) {
        this.dataTypes = dataTypes;
    }

    @Override
    public Map<String, FHIRImplementationGuide> getFhirImplementationGuides() {
        return fhirImplementationGuides;
    }

    public void addFhirImplementationGuide(String igName, FHIRR4ImplementationGuide implementationGuide) {
        fhirImplementationGuides.putIfAbsent(igName, implementationGuide);
    }

    public void setFhirImplementationGuides(Map<String, FHIRImplementationGuide> fhirImplementationGuides) {
        this.fhirImplementationGuides = fhirImplementationGuides;
    }

    public void addValueSet(String id, FHIRTerminologyDef terminologyDef) {
        this.valueSets.putIfAbsent(id, terminologyDef);
    }

    public Map<String, FHIRTerminologyDef> getValueSets() {
        return valueSets;
    }

    public void setValueSets(Map<String, FHIRTerminologyDef> valueSets) {
        this.valueSets = valueSets;
    }

    public void addCodeSystem(String id, FHIRTerminologyDef terminologyDef) {
        this.codeSystems.putIfAbsent(id, terminologyDef);
    }

    public Map<String, FHIRTerminologyDef> getCodeSystems() {
        return codeSystems;
    }

    public void setCodeSystems(Map<String, FHIRTerminologyDef> codeSystems) {
        this.codeSystems = codeSystems;
    }

    public void setTerminologies() {
        this.resolvedTerminologies = FHIRR4SpecUtils.resolveTerminology(this.valueSets, this.codeSystems);
    }

    public Map<String, Map<String,Coding>> getTerminologies() {
        return resolvedTerminologies;
    }

    public boolean isPrimitiveDataType(ElementDefinition elementDefinition) {
        if (!FHIRR4SpecUtils.isMultiDataType(elementDefinition)) {
            String typeCode = FHIRR4SpecUtils.getTypeCodeOfElementDef(elementDefinition);
            FHIRR4DataTypeDef dataTypeDef = (FHIRR4DataTypeDef) dataTypes.get(typeCode);
            return dataTypeDef != null && R4DefKind.PRIMARY_TYPE.equals(dataTypeDef.getKind());
        }
        return false;
    }

    public boolean isMultiDatatype(ElementDefinition elementDefinition) {
        return FHIRR4SpecUtils.isMultiDataType(elementDefinition);
    }

    public void addInternationalSearchParameter(String resourceType, FHIRR4SearchParamDef searchParameter) {
        if (!internationalSpecSearchParameters.containsKey(resourceType)) {
            internationalSpecSearchParameters.put(resourceType, new ArrayList<>() {
                {
                    add(searchParameter);
                }
            });
        } else {
            internationalSpecSearchParameters.get(resourceType).add(searchParameter);
        }
    }

    public List<FHIRR4SearchParamDef> getInternationalSearchParameters(String resourceType) {
        return internationalSpecSearchParameters.getOrDefault(resourceType, Collections.emptyList());
    }
}
