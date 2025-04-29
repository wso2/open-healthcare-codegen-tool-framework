package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;

import java.util.List;

public interface FHIRSearchParamDef extends SpecModel {
    Object getSearchParameter();

    void setSearchParameter(Object searchParameter);

    List<String> getBaseResources();
}
