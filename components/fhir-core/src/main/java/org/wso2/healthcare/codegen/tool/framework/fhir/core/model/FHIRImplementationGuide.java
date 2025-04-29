package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model.APIDefinition;

import java.util.HashMap;
import java.util.Map;

public class FHIRImplementationGuide {
    private String name;
    private String id;
    private Map<String, FHIRResourceDef> resources;
    private Map<String, FHIRSearchParamDef> searchParameters;
    private Map<String, FHIROperationDef> operations;
    private Map<String, APIDefinition> apiDefinitions;

    public FHIRImplementationGuide() {
        resources = new HashMap<>();
        searchParameters = new HashMap<>();
        operations = new HashMap<>();
        apiDefinitions = new HashMap<>();
    }

    public Map<String, APIDefinition> getApiDefinitions() {
        return apiDefinitions;
    }

    public void addApiDefinition(String key, APIDefinition apiDefinition) {
        if (apiDefinitions.containsKey(key)) {
            APIDefinition currentApiDef = apiDefinitions.get(key);
            currentApiDef.getOpenAPI().getComponents().getSchemas().putAll(
                    apiDefinition.getOpenAPI().getComponents().getSchemas());
        } else {
            apiDefinitions.put(key, apiDefinition);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, FHIRResourceDef> getResources() {
        return resources;
    }

    public void setResources(Map<String, FHIRResourceDef> resources) {
        this.resources = resources;
    }

    public Map<String, FHIRSearchParamDef> getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(Map<String, FHIRSearchParamDef> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public Map<String, FHIROperationDef> getOperations() {
        return operations;
    }

    public void setOperations(Map<String, FHIROperationDef> operations) {
        this.operations = operations;
    }
}
