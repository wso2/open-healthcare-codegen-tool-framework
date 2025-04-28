package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model;

import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIROperationDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.ElementExpansionType;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common.FHIRR5SpecUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds the FHIR R5 resource definition.
 */

public class FHIRR5ResourceDef implements FHIRResourceDef <StructureDefinition, Extension, ElementDefinition> {

    private StructureDefinition definition;
    private DefKind kind;
    private final Map<String, FHIRSearchParamDef> searchParamDefs = new HashMap<>();
    private final Map<String, FHIROperationDef> operationDefMap = new HashMap<>();
    private FHIRR5ResourceDef parentResource;

    @Override
    public StructureDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(StructureDefinition definition) {
        this.definition = definition;
    }

    @Override
    public List<Extension> getExtensions() {
        return definition.getExtension();
    }

    @Override
    public Map<String, FHIRSearchParamDef> getSearchParameters() {
        return searchParamDefs;
    }

    @Override
    public Map<String, FHIROperationDef> getOperations() {
        return operationDefMap;
    }

    @Override
    public void addSearchParam(String url, FHIRSearchParamDef searchParam) {
        searchParamDefs.put(url, searchParam);
    }

    @Override
    public FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> parentResource) {
        this.parentResource = (FHIRR5ResourceDef) parentResource;
    }

    @Override
    public StructureDefinition getDataType(String fhirPath) {
        return null;
    }

    /**
     * Returns elements of the FHIR resource, given the expansion type: snapshot|differential.
     *
     * @param expansionType values: snapshot|differential
     * @return elements of the FHIR resource
     */
    @Override
    public List<ElementDefinition> getElements(ElementExpansionType expansionType) {
        List<ElementDefinition> elementDefinitions = null;
        List<ElementDefinition> processableElementDefinitions = null;

        if (expansionType.equals(ElementExpansionType.SNAPSHOT)) {
            elementDefinitions = definition.getSnapshot().getElement();
            processableElementDefinitions = new ArrayList<>();
        }
        else if (expansionType.equals(ElementExpansionType.DIFFERENTIAL)) {
            elementDefinitions = definition.getDifferential().getElement();
            processableElementDefinitions = new ArrayList<>();
        }

        if (elementDefinitions != null) {
            for (ElementDefinition elementDefinition : elementDefinitions) {
                if (FHIRR5SpecUtils.canSkip(definition, elementDefinition)) {
                    continue;
                }
                processableElementDefinitions.add(elementDefinition);
            }
        }
        return processableElementDefinitions;
    }

    @Override
    public String getDefinitionType() {
        return this.definition.getKind().toCode();
    }

    /**
     * Returns resource type kind. values("resource", "logical", "invalid").
     *
     * @return Resource type kind
     */
    @Override
    public DefKind getKind() {
        return kind;
    }

    @Override
    public void setKind(DefKind kind) {
        this.kind = kind;
    }
}
