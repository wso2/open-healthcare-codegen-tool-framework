package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;

public interface FHIRTerminologyDef extends SpecModel {
    // Generalized Resource to Object as there are R4 Resource and R5 Resource
    Object getTerminologyResource();

    void setTerminologyResource(Object terminologyResource);

    String getUrl();

    void setUrl(String url);
}