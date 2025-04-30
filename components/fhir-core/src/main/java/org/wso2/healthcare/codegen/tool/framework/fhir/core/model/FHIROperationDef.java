package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import java.util.List;

public interface FHIROperationDef <O> {

    O getOperationDefinition();

    void setOperationDefinition(O operationDefinition);

    void populateTargetResources(O operationDefinition);

    List<String> getTargetResources();
}
