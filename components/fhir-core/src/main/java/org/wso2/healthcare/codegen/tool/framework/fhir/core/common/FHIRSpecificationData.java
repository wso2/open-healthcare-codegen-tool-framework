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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.common;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.wso2.healthcare.codegen.tool.framework.commons.core.SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRDataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

import java.util.HashMap;
import java.util.Map;

public class FHIRSpecificationData implements SpecificationData {

    private static final FHIRSpecificationData DATA_HOLDER_INSTANCE = new FHIRSpecificationData();

    private Map<String, FHIRDataTypeDef> dataTypes;
    private Map<String, FHIRImplementationGuide> fhirImplementationGuides;
    private Map<String, FHIRTerminologyDef> valueSets;
    private Map<String, FHIRTerminologyDef> codeSystems;
    private Map<String, Map<String,Coding>> resolvedTerminologies;

    public static FHIRSpecificationData getDataHolderInstance() {
        return DATA_HOLDER_INSTANCE;
    }

    public FHIRSpecificationData() {
        dataTypes = new HashMap<>();
        fhirImplementationGuides = new HashMap<>();
        valueSets = new HashMap<>();
        codeSystems = new HashMap<>();
    }

    public Map<String, FHIRDataTypeDef> getDataTypes() {
        return dataTypes;
    }

    public void addDataType(String id, FHIRDataTypeDef dataTypeDef) {
        dataTypes.putIfAbsent(id, dataTypeDef);
    }

    public void setDataTypes(Map<String, FHIRDataTypeDef> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public Map<String, FHIRImplementationGuide> getFhirImplementationGuides() {
        return fhirImplementationGuides;
    }

    public void addFhirImplementationGuide(String igName, FHIRImplementationGuide implementationGuide) {
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
        this.resolvedTerminologies = FHIRSpecUtils.resolveTerminology(this.valueSets, this.codeSystems);
    }

    public Map<String, Map<String,Coding>> getTerminologies() {
        return resolvedTerminologies;
    }

    public boolean isPrimitiveDataType(ElementDefinition elementDefinition) {
        if (!FHIRSpecUtils.isMultiDataType(elementDefinition)) {
            String typeCode = FHIRSpecUtils.getTypeCodeOfElementDef(elementDefinition);
            FHIRDataTypeDef dataTypeDef = dataTypes.get(typeCode);
            return dataTypeDef != null && DefKind.PRIMARY_TYPE.equals(dataTypeDef.getKind());
        }
        return false;
    }

    public boolean isMultiDatatype(ElementDefinition elementDefinition) {
        return FHIRSpecUtils.isMultiDataType(elementDefinition);
    }
}
