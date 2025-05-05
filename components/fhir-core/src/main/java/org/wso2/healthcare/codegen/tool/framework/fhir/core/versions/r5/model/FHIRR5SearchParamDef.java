package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model;

import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.SearchParameter;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;

import java.util.ArrayList;
import java.util.List;

public class FHIRR5SearchParamDef implements FHIRSearchParamDef <SearchParameter> {
    private SearchParameter searchParameter;

    public FHIRR5SearchParamDef(){}

    public FHIRR5SearchParamDef(SearchParameter searchParameter){
        this.searchParameter = searchParameter;
    }

    @Override
    public SearchParameter getSearchParameter() {
        return searchParameter;
    }

    @Override
    public void setSearchParameter(SearchParameter searchParameter) {
        this.searchParameter = searchParameter;
    }

    @Override
    public List<String> getBaseResources(){
        List<String> baseResources = new ArrayList<>();
        List<CodeType> baseResourceTypes = this.searchParameter.getBase();

        for(CodeType baseResourceType : baseResourceTypes){
            baseResources.add(baseResourceType.getValue());
        }

        return baseResources;
    }
}
