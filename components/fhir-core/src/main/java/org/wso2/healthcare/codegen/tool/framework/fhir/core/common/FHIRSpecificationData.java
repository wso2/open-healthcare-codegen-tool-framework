package org.wso2.healthcare.codegen.tool.framework.fhir.core.common;

import org.wso2.healthcare.codegen.tool.framework.commons.core.SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRDataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;

import java.util.Map;

public abstract class FHIRSpecificationData implements SpecificationData {
    public Map<String, FHIRImplementationGuide> getFhirImplementationGuides() {
        return null;
    }

    public Map<String, FHIRDataTypeDef> getDataTypes() {
        return null;
    }
}
