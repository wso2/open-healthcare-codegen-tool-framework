package org.wso2.healthcare.codegen.tool.framework.fhir.core;

import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.FHIRR4ToolContext;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.common.FHIRR4SpecificationData;

public class FHIRToolContextFactory {
    public static FHIRToolContext getToolContext(String fhirVersion){
        switch (fhirVersion.toLowerCase()) {
            case "r4": {
                FHIRR4ToolContext fhirR4ToolContext = new FHIRR4ToolContext();
                fhirR4ToolContext.setSpecificationData(FHIRR4SpecificationData.getDataHolderInstance());
                return fhirR4ToolContext;
            }
            case "r5": {
                return null;
            }
            default: {
                throw new IllegalArgumentException("Unsupported FHIR version. Only R4 and R5 are supported.");
            }
        }
    }
}
