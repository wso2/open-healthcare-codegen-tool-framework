package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.oas.model.R4APIDefinition;

import java.util.Map;

public abstract class FHIRImplementationGuide {

    public String getName() {
        return "";
    }

    public Map<String, FHIRResourceDef> getResources() {
        return null;
    }

    public Map<String, FHIRSearchParamDef> getSearchParameters() {
        return null;
    }

    // CHANGE TO SUPPORT BOTH NOT ONLY R4
    public Map<String, R4APIDefinition> getApiDefinitions() {
        return null;
    }
}
