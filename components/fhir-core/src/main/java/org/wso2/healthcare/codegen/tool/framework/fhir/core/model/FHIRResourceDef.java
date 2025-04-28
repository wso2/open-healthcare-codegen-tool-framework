package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.ElementExpansionType;

import java.util.List;
import java.util.Map;

// S = StructureDefinition
// E = Extension
// L = ElementDefinition

public interface FHIRResourceDef <S, E, L> extends SpecModel {

    S getDefinition();

    void setDefinition(S definition);

    List<E> getExtensions();

    Map<String, FHIRSearchParamDef> getSearchParameters();

    Map<String, FHIROperationDef> getOperations();

    void addSearchParam(String url, FHIRSearchParamDef searchParam);

    FHIRResourceDef <S, E, L> getParentResource();

    void setParentResource(FHIRResourceDef <S, E, L> parentResource);

    S getDataType(String fhirPath);

    List<L> getElements(ElementExpansionType expansionType);

    String getDefinitionType();

    /**
     * Returns resource type kind. values("resource", "logical", "invalid").
     *
     * @return Resource type kind
     */
    DefKind getKind();

    void setKind(DefKind kind);
}
