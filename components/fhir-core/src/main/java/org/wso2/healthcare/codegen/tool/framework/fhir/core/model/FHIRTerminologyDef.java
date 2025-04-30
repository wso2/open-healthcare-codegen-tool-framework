package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;

/**
 * This class holds FHIR Terminology definition model.
 */

// R = Resource
public interface FHIRTerminologyDef <R> extends SpecModel {
    // Generalized Resource to Object as there are R4 Resource and R5 Resource
    R getTerminologyResource();

    void setTerminologyResource(R terminologyResource);

    String getUrl();

    void setUrl(String url);
}