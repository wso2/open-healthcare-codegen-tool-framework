package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5;

import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.core.SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.AbstractFHIRToolContext;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.FHIRToolConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common.FHIRR5SpecificationData;

public class FHIRR5ToolContext extends AbstractFHIRToolContext {

    private FHIRToolConfig config;
    private FHIRSpecificationData specificationData;

    @Override
    public void setConfig(ToolConfig toolConfig) {
        this.config = (FHIRToolConfig) toolConfig;
    }

    @Override
    public FHIRToolConfig getConfig() {
        return this.config;
    }

    @Override
    public FHIRSpecificationData getSpecificationData() {
        return this.specificationData;
    }

    @Override
    public void setSpecificationData(SpecificationData specificationData) {
        this.specificationData = (FHIRR5SpecificationData) specificationData;
    }
}
