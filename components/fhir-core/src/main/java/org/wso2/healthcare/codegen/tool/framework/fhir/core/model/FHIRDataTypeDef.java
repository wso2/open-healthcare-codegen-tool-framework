package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.hl7.fhir.r4.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;

public abstract class FHIRDataTypeDef implements SpecModel {
    public StructureDefinition getDefinition() {
        return null;
    }
}
