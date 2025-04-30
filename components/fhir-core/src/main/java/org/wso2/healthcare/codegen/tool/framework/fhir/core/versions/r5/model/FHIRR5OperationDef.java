package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model;

import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.OperationDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIROperationDef;

import java.util.ArrayList;
import java.util.List;

public class FHIRR5OperationDef implements FHIROperationDef <OperationDefinition> {

    private OperationDefinition operationDefinition;
    private List<String> targetResources;

    @Override
    public OperationDefinition getOperationDefinition() {
        return operationDefinition;
    }

    @Override
    public void setOperationDefinition(OperationDefinition operationDefinition) {
        this.operationDefinition = operationDefinition;
        populateTargetResources(operationDefinition);
    }

    @Override
    public void populateTargetResources(OperationDefinition operationDefinition) {
        List<String> resources = new ArrayList<>();
        // CodeType: Store values from coding systems (like LOINC or ICD) in OperationDefinition
        for(CodeType resource: operationDefinition.getResource()){
            resources.add(resource.getCode());
        }
        this.targetResources = resources;
    }

    @Override
    public List<String> getTargetResources() {
        return targetResources;
    }
}
