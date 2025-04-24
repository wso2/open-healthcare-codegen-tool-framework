package org.wso2.healthcare.codegen.tool.framework.fhir.core;

import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.FHIRR4ToolContext;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common.FHIRR4SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.FHIRR5ToolContext;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common.FHIRR5SpecificationData;

public class FHIRToolContextFactory {
    public static FHIRToolContext getToolContext(String fhirVersion){
        switch (fhirVersion.toLowerCase()) {
            case "r4": {
                FHIRR4ToolContext fhirR4ToolContext = new FHIRR4ToolContext();
                fhirR4ToolContext.setSpecificationData(FHIRR4SpecificationData.getDataHolderInstance());
                return fhirR4ToolContext;
            }
            case "r5": {
                FHIRR5ToolContext fhirr5ToolContext = new FHIRR5ToolContext();
                fhirr5ToolContext.setSpecificationData(FHIRR5SpecificationData.getDataHolderInstance());
                return fhirr5ToolContext;
            }
            default: {
                throw new IllegalArgumentException("Unsupported FHIR version. Only R4 and R5 are supported.");
            }
        }
    }
}
