package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;

import java.util.List;

// S - SearchParameter
public interface FHIRSearchParamDef <S> extends SpecModel {
    S getSearchParameter();

    void setSearchParameter(S searchParameter);

    List<String> getBaseResources();
}
