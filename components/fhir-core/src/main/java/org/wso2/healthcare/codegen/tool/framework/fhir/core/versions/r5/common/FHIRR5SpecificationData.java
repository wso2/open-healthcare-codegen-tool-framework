package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common;

import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Coding;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRDataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5DataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5ImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5SearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5TerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.util.R5DefKind;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class FHIRR5SpecificationData extends FHIRSpecificationData {

    private static final FHIRR5SpecificationData DATA_HOLDER_INSTANCE = new FHIRR5SpecificationData();

    private Map<String, FHIRDataTypeDef> dataTypes;
    private Map<String, FHIRImplementationGuide> fhirImplementationGuides;
    private Map<String, FHIRTerminologyDef> valueSets;
    private Map<String, FHIRTerminologyDef> codeSystems;
    private Map<String, Map<String, Coding>> resolvedTerminologies;
    private final HashMap<String, List<FHIRSearchParamDef>> internationalSpecSearchParameters = new HashMap<>();

    public static FHIRR5SpecificationData getDataHolderInstance() {
        return DATA_HOLDER_INSTANCE;
    }

    private FHIRR5SpecificationData() {
        dataTypes = new HashMap<>();
        fhirImplementationGuides = new HashMap<>();
        valueSets = new HashMap<>();
        codeSystems = new HashMap<>();
    }

    @Override
    public Map<String, FHIRDataTypeDef> getDataTypes() {
        return dataTypes;
    }

    public void addDataType(String id, FHIRR5DataTypeDef dataTypeDef) {
        dataTypes.putIfAbsent(id, dataTypeDef);
    }

    public void setDataTypes(Map<String, FHIRDataTypeDef> dataTypes) {
        this.dataTypes = dataTypes;
    }

    @Override
    public Map<String, FHIRImplementationGuide> getFhirImplementationGuides() {
        return fhirImplementationGuides;
    }

    public void addFhirImplementationGuide(String id, FHIRR5ImplementationGuide fhirImplementationGuide) {
        fhirImplementationGuides.putIfAbsent(id, fhirImplementationGuide);
    }

    public void setFhirImplementationGuides(Map<String, FHIRImplementationGuide> fhirImplementationGuides) {
        this.fhirImplementationGuides = fhirImplementationGuides;
    }

    public void addValueSet(String id, FHIRR5TerminologyDef terminologyDef) {
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
        this.resolvedTerminologies = FHIRR5SpecUtils.resolveTerminology(this.valueSets, this.codeSystems);
    }

    public Map<String, Map<String, Coding>> getTerminologies() {
        return resolvedTerminologies;
    }

    public boolean isPrimitiveDataType(ElementDefinition elementDefinition) {
        if (!FHIRR5SpecUtils.isMultiDataType(elementDefinition)) {
            String typeCode = FHIRR5SpecUtils.getTypeCodeOfElementDef(elementDefinition);
            FHIRR5DataTypeDef dataTypeDef = (FHIRR5DataTypeDef) dataTypes.get(typeCode);
            return dataTypeDef != null && R5DefKind.PRIMARY_TYPE.equals(dataTypeDef.getKind());
        }
        return false;
    }

    public boolean isMultiDatatype(ElementDefinition elementDefinition) {
        return FHIRR5SpecUtils.isMultiDataType(elementDefinition);
    }

    public void addInternationalSearchParameter(String resourceType, FHIRR5SearchParamDef searchParameter) {
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

    // FOR R5OASGenerator
//    public List<FHIRR5SearchParamDef> getInternationalSearchParameters(String resourceType) {
//        return internationalSpecSearchParameters.getOrDefault(resourceType, Collections.emptyList());
//    }
}
