package org.wso2.healthcare.codegen.tool.framework.fhir.core.common;

import org.wso2.healthcare.codegen.tool.framework.commons.core.SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRDataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FHIRSpecificationData implements SpecificationData {
    protected Map<String, FHIRDataTypeDef> dataTypes;
    protected Map<String, FHIRTerminologyDef> valueSets;
    protected Map<String, FHIRTerminologyDef> codeSystems;
    private Map<String, FHIRImplementationGuide> fhirImplementationGuides;
    private final HashMap<String, List<FHIRSearchParamDef>> internationalSpecSearchParameters = new HashMap<>();

    public FHIRSpecificationData (){
        this.dataTypes = new HashMap<>();
        this.fhirImplementationGuides = new HashMap<>();
        this.valueSets = new HashMap<>();
        this.codeSystems = new HashMap<>();
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

    public void addInternationalSearchParameter(String resourceType, FHIRSearchParamDef searchParameter) {
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

    public List<FHIRSearchParamDef> getInternationalSearchParameters(String resourceType) {
        return internationalSpecSearchParameters.getOrDefault(resourceType, Collections.emptyList());
    }
}
