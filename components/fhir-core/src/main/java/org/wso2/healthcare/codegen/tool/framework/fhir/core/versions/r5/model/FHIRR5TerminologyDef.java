package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model;

import org.hl7.fhir.r5.model.Resource;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;

public class FHIRR5TerminologyDef implements FHIRTerminologyDef {
    private String url;
    private Resource terminologyResource;

    @Override
    public Resource getTerminologyResource() {
        return terminologyResource;
    }

    @Override
    public void setTerminologyResource(Object terminologyResource) {
        this.terminologyResource = (Resource) terminologyResource;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }
}
