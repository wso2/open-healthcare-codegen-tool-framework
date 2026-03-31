/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.healthcare.codegen.tool.framework.fhir.core.oas;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.FHIRTool;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model.APIDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class generates OAS definitions for FHIR resources.
 */
public class OASGenerator {
    protected OpenAPI fhirOASBaseStructure;

    private static final Log LOG = LogFactory.getLog(OASGenerator.class);

    public OASGenerator(){
        try{
            populateFhirOASBaseStructure();
        }
        catch (IOException e){
            LOG.error("Error occurred while getting the base OAS structure.", e);
        }
    }

    /**
     * Populates base FHIR OAS definition structure.
     */
    private void populateFhirOASBaseStructure() throws IOException {
        fhirOASBaseStructure = new OpenAPI();
        try (InputStream inputStream = FHIRTool.class.getClassLoader().getResourceAsStream(
                "api-defs/oas-static-content.yaml")) {
            if (inputStream != null) {
                String parsedYamlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                OpenAPI staticOASContent = new OpenAPIV3Parser().readContents(parsedYamlContent).getOpenAPI();
                Components components = new Components();
                components.setParameters(staticOASContent.getComponents().getParameters());
                components.securitySchemes(staticOASContent.getComponents().getSecuritySchemes());
                components.setSchemas(staticOASContent.getComponents().getSchemas());
                fhirOASBaseStructure.setComponents(components);
            }
        }
    }

    public OpenAPI getFhirOASBaseStructure() {
        return fhirOASBaseStructure;
    }

    /**
     * Creates a deep copy of the base Components for resource-specific modifications.
     * This ensures that security scheme modifications don't affect other resources.
     *
     * @return A new Components instance with copied security schemes
     */
    protected Components cloneBaseComponents() {
        Components originalComponents = fhirOASBaseStructure.getComponents();
        if (originalComponents == null) {
            return new Components();
        }

        Components clonedComponents = new Components();

        // Copy parameters (these are shared and don't need resource-specific values)
        if (originalComponents.getParameters() != null) {
            clonedComponents.setParameters(originalComponents.getParameters());
        }

        // Copy schemas (these are shared and don't need resource-specific values)
        if (originalComponents.getSchemas() != null) {
            clonedComponents.setSchemas(originalComponents.getSchemas());
        }

        // Clone security schemes (these need resource-specific values)
        if (originalComponents.getSecuritySchemes() != null) {
            Map<String, SecurityScheme> clonedSecuritySchemes = new HashMap<>();
            for (Map.Entry<String, SecurityScheme> entry : originalComponents.getSecuritySchemes().entrySet()) {
                SecurityScheme originalScheme = entry.getValue();
                SecurityScheme clonedScheme = new SecurityScheme();
                clonedScheme.setType(originalScheme.getType());
                clonedScheme.setDescription(originalScheme.getDescription());
                clonedScheme.setName(originalScheme.getName());
                clonedScheme.setIn(originalScheme.getIn());
                clonedScheme.setScheme(originalScheme.getScheme());
                clonedScheme.setBearerFormat(originalScheme.getBearerFormat());
                clonedScheme.setOpenIdConnectUrl(originalScheme.getOpenIdConnectUrl());

                // Clone OAuth flows
                if (originalScheme.getFlows() != null) {
                    OAuthFlows clonedFlows = new OAuthFlows();
                    if (originalScheme.getFlows().getAuthorizationCode() != null) {
                        clonedFlows.setAuthorizationCode(cloneOAuthFlow(originalScheme.getFlows().getAuthorizationCode()));
                    }
                    if (originalScheme.getFlows().getImplicit() != null) {
                        clonedFlows.setImplicit(cloneOAuthFlow(originalScheme.getFlows().getImplicit()));
                    }
                    if (originalScheme.getFlows().getPassword() != null) {
                        clonedFlows.setPassword(cloneOAuthFlow(originalScheme.getFlows().getPassword()));
                    }
                    if (originalScheme.getFlows().getClientCredentials() != null) {
                        clonedFlows.setClientCredentials(cloneOAuthFlow(originalScheme.getFlows().getClientCredentials()));
                    }
                    clonedScheme.setFlows(clonedFlows);
                }

                clonedSecuritySchemes.put(entry.getKey(), clonedScheme);
            }
            clonedComponents.setSecuritySchemes(clonedSecuritySchemes);
        }

        // Copy request bodies (these are shared)
        if (originalComponents.getRequestBodies() != null) {
            clonedComponents.setRequestBodies(originalComponents.getRequestBodies());
        }

        return clonedComponents;
    }

    /**
     * Creates a deep copy of an OAuthFlow object.
     *
     * @param original The original OAuthFlow to clone
     * @return A new OAuthFlow instance with copied values
     */
    private OAuthFlow cloneOAuthFlow(OAuthFlow original) {
        if (original == null) {
            return null;
        }

        OAuthFlow cloned = new OAuthFlow();
        cloned.setAuthorizationUrl(original.getAuthorizationUrl());
        cloned.setTokenUrl(original.getTokenUrl());
        cloned.setRefreshUrl(original.getRefreshUrl());

        // Clone scopes
        if (original.getScopes() != null) {
            Scopes clonedScopes = new Scopes();
            for (Map.Entry<String, String> scopeEntry : original.getScopes().entrySet()) {
                clonedScopes.addString(scopeEntry.getKey(), scopeEntry.getValue());
            }
            cloned.setScopes(clonedScopes);
        }

        // Clone extensions
        if (original.getExtensions() != null) {
            Map<String, Object> clonedExtensions = new HashMap<>();
            for (Map.Entry<String, Object> extEntry : original.getExtensions().entrySet()) {
                if (extEntry.getValue() instanceof Map) {
                    Map<String, Object> clonedMap = new LinkedHashMap<>((Map<String, Object>) extEntry.getValue());
                    clonedExtensions.put(extEntry.getKey(), clonedMap);
                } else {
                    clonedExtensions.put(extEntry.getKey(), extEntry.getValue());
                }
            }
            cloned.setExtensions(clonedExtensions);
        }

        return cloned;
    }

    /**
     * Replaces the &lt;ResourceType&gt; placeholder in security schemes with the actual resource type.
     *
     * @param components Components object containing security schemes
     * @param resourceType The FHIR resource type (e.g., Patient, Observation)
     */
    protected void replaceResourceTypeInSecuritySchemes(Components components, String resourceType) {
        if (components == null || components.getSecuritySchemes() == null) {
            return;
        }

        for (Map.Entry<String, SecurityScheme> schemeEntry : components.getSecuritySchemes().entrySet()) {
            SecurityScheme securityScheme = schemeEntry.getValue();
            if (securityScheme.getFlows() != null) {
                OAuthFlows flows = securityScheme.getFlows();

                // Handle AuthorizationCode flow
                if (flows.getAuthorizationCode() != null) {
                    replaceResourceTypeInOAuthFlow(flows.getAuthorizationCode(), resourceType);
                }

                // Handle Implicit flow
                if (flows.getImplicit() != null) {
                    replaceResourceTypeInOAuthFlow(flows.getImplicit(), resourceType);
                }

                // Handle Password flow
                if (flows.getPassword() != null) {
                    replaceResourceTypeInOAuthFlow(flows.getPassword(), resourceType);
                }

                // Handle ClientCredentials flow
                if (flows.getClientCredentials() != null) {
                    replaceResourceTypeInOAuthFlow(flows.getClientCredentials(), resourceType);
                }
            }
        }
    }

    /**
     * Replaces the &lt;ResourceType&gt; placeholder in an OAuth flow's scopes and extensions.
     *
     * @param flow OAuth flow object
     * @param resourceType The FHIR resource type
     */
    private void replaceResourceTypeInOAuthFlow(OAuthFlow flow, String resourceType) {
        if (flow == null) {
            return;
        }

        // Replace in scopes
        if (flow.getScopes() != null) {
            Scopes updatedScopes = new Scopes();
            for (Map.Entry<String, String> scopeEntry : flow.getScopes().entrySet()) {
                String scopeKey = scopeEntry.getKey().replace("<ResourceType>", resourceType);
                String scopeValue = scopeEntry.getValue().replace("<ResourceType>", resourceType);
                updatedScopes.addString(scopeKey, scopeValue);
            }
            flow.setScopes(updatedScopes);
        }

        // Replace in extensions (for x-scopes-bindings)
        if (flow.getExtensions() != null) {
            Map<String, Object> updatedExtensions = new HashMap<>();
            for (Map.Entry<String, Object> extensionEntry : flow.getExtensions().entrySet()) {
                String extensionKey = extensionEntry.getKey();
                Object extensionValue = extensionEntry.getValue();

                if (extensionValue instanceof Map) {
                    Map<String, Object> bindingsMap = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> bindingEntry : ((Map<String, Object>) extensionValue).entrySet()) {
                        String bindingKey = bindingEntry.getKey().replace("<ResourceType>", resourceType);
                        bindingsMap.put(bindingKey, bindingEntry.getValue());
                    }
                    updatedExtensions.put(extensionKey, bindingsMap);
                } else {
                    updatedExtensions.put(extensionKey, extensionValue);
                }
            }
            flow.setExtensions(updatedExtensions);
        }
    }

    /**
     * Populates OAS info object.
     *
     * @param apiDefinition API definition object
     */
    public void populateOASInfo(APIDefinition apiDefinition) {

        Info info = new Info();
        info.setTitle(apiDefinition.getResourceType());
        info.setVersion("1.0.0");

        //TODO: check whether these needs to be provided via config vars
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");

        Contact contact = new Contact();
        contact.setName("API Support");
        contact.setUrl("https://wso2.com/contact/`");
        contact.setEmail("user@email.com");
        info.setContact(contact);
        apiDefinition.getOpenAPI().setInfo(info);
    }

    /**
     * Generates security scopes for a specific operation type.
     * Includes both resource-specific scopes and wildcard scopes.
     *
     * @param resourceType The FHIR resource type
     * @param operationType The operation type (read, create, update, delete, search)
     * @return SecurityRequirement with appropriate scopes
     */
    protected SecurityRequirement generateSecurityScopes(String resourceType, String operationType) {
        List<String> scopes = new ArrayList<>();
        String scopeSuffix = switch (operationType) {
            case "create" -> ".c";
            case "update", "patch" -> ".u";
            case "delete" -> ".d";
            case "search" -> ".s";
            default -> ".r";
        };

        // Add resource-specific scopes
        scopes.add("patient/" + resourceType + scopeSuffix);
        scopes.add("patient/*" + scopeSuffix);
        scopes.add("user/" + resourceType + scopeSuffix);
        scopes.add("user/*" + scopeSuffix);
        scopes.add("system/" + resourceType + scopeSuffix);
        scopes.add("system/*" + scopeSuffix);

        return new SecurityRequirement().addList("default", scopes);
    }

    /**
     * Populates OAS paths object.
     *
     * @param apiDefinition API definition object
     */
    protected void populateOASPaths(APIDefinition apiDefinition) {

        Paths paths = new Paths();
        Map<String, String> interactions = new HashMap<>() {{
            put("read", "GET");
            put("search", "GET");
            put("write", "POST");
            put("update", "PUT");
            put("delete", "DELETE");
            put("patch", "PATCH");
        }};

        PathItem rootPath = new PathItem();
        PathItem idPath = new PathItem();

        for (Map.Entry<String, String> interaction : interactions.entrySet()) {
            Operation operation = new Operation();

            switch (interaction.getKey()) {
                case "read":
                    operation.addTagsItem(interaction.getValue());
                    operation.addTagsItem(apiDefinition.getResourceType());
                    operation.addSecurityItem(generateSecurityScopes(
                            apiDefinition.getResourceType(), "read"));
                    operation.addExtension("x-auth-type", "Application & Application User");

                    ApiResponses getResponses = new ApiResponses();
                    ApiResponse readSuccessResponse = new ApiResponse();
                    readSuccessResponse.setDescription(interaction.getKey() + " " + apiDefinition.getResourceType() + " operation successful");

                    Content successContent = new Content();
                    MediaType mediaType = new MediaType();
                    Schema schema = new Schema();
                    schema.$ref(APIDefinitionConstants.OAS_REF_SCHEMAS + apiDefinition.getResourceType());
                    operation.addParametersItem(OASGenUtils.generateParameter(
                            "id", "logical identifier", "string", "path", true));
                    mediaType.setSchema(schema);
                    successContent.addMediaType(APIDefinitionConstants.CONTENT_TYPE_FHIR_JSON, mediaType);
                    readSuccessResponse.setContent(successContent);
                    getResponses.addApiResponse("200", readSuccessResponse);
                    operation.setResponses(getResponses);
                    idPath.setGet(operation);
                    break;

                case "search":
                    operation.addTagsItem(interaction.getValue());
                    operation.addTagsItem(apiDefinition.getResourceType());
                    operation.addSecurityItem(generateSecurityScopes(
                            apiDefinition.getResourceType(), "search"));
                    operation.addExtension("x-auth-type", "Application & Application User");

                    ApiResponses searchResponses = new ApiResponses();
                    ApiResponse searchSuccessResponse = new ApiResponse();
                    searchSuccessResponse.setDescription(
                            interaction.getKey() + " " + apiDefinition.getResourceType() + " operation successful");

                    Content searchSuccessContent = new Content();
                    MediaType searchMediaType = new MediaType();
                    Schema searchSchema = new Schema();
                    searchSchema.$ref(APIDefinitionConstants.OAS_REF_SCHEMAS + apiDefinition.getResourceType());
                    searchSchema.$ref(APIDefinitionConstants.OAS_REF_SCHEMAS + "Bundle");
                    searchMediaType.setSchema(searchSchema);
                    searchSuccessContent.addMediaType(APIDefinitionConstants.CONTENT_TYPE_FHIR_JSON, searchMediaType);
                    searchSuccessResponse.setContent(searchSuccessContent);
                    searchResponses.addApiResponse("200", searchSuccessResponse);
                    operation.setResponses(searchResponses);
                    rootPath.setGet(operation);
                    break;

                case "create":
                    operation.addTagsItem(interaction.getValue());
                    operation.addTagsItem(apiDefinition.getResourceType());
                    operation.addSecurityItem(generateSecurityScopes(apiDefinition.getResourceType(), "create"));
                    operation.addExtension("x-auth-type", "Application & Application User");

                    ApiResponses postResponses = new ApiResponses();
                    ApiResponse createSuccessResponse = new ApiResponse();
                    RequestBody requestBody = new RequestBody();
                    requestBody.$ref(APIDefinitionConstants.OAS_REF_REQUEST_BODIES + apiDefinition.getResourceType());
                    createSuccessResponse.setDescription(
                            interaction.getKey() + " " + apiDefinition.getResourceType() + " operation successful");
                    postResponses.addApiResponse("201", createSuccessResponse);
                    operation.setResponses(postResponses);
                    rootPath.setPost(operation);
                    break;

                case "update":
                    operation.addTagsItem(interaction.getValue());
                    operation.addTagsItem(apiDefinition.getResourceType());
                    operation.addSecurityItem(generateSecurityScopes(
                            apiDefinition.getResourceType(), "update"));
                    operation.addExtension("x-auth-type", "Application & Application User");

                    ApiResponses putResponses = new ApiResponses();
                    ApiResponse updateSuccessResponse = new ApiResponse();
                    RequestBody putRequestBody = new RequestBody();

                    putRequestBody.$ref(APIDefinitionConstants.OAS_REF_REQUEST_BODIES + apiDefinition.getResourceType());
                    updateSuccessResponse.setDescription(
                            interaction.getKey() + " " + apiDefinition.getResourceType() + " operation successful");
                    putResponses.addApiResponse("200", updateSuccessResponse);
                    operation.setResponses(putResponses);
                    operation.addParametersItem(OASGenUtils.generateParameter(
                            "id", "logical identifier", "string", "path", true));
                    idPath.setPut(operation);
                    break;

                case "patch":
                    operation.addTagsItem(interaction.getValue());
                    operation.addTagsItem(apiDefinition.getResourceType());
                    operation.addSecurityItem(generateSecurityScopes(
                            apiDefinition.getResourceType(), "patch"));
                    operation.addExtension("x-auth-type", "Application & Application User");

                    ApiResponses patchResponses = new ApiResponses();
                    ApiResponse patchSuccessResponse = new ApiResponse();
                    RequestBody patchRequestBody = new RequestBody();
                    patchRequestBody.$ref(APIDefinitionConstants.OAS_REF_REQUEST_BODIES + apiDefinition.getResourceType());
                    patchSuccessResponse.setDescription(
                            interaction.getKey() + " " + apiDefinition.getResourceType() + " operation successful");
                    patchResponses.addApiResponse("200", patchSuccessResponse);
                    operation.setResponses(patchResponses);
                    operation.addParametersItem(OASGenUtils.generateParameter(
                            "id", "logical identifier", "string", "path", true));
                    idPath.setPatch(operation);
                    break;

                case "delete":
                    operation.addTagsItem(interaction.getValue());
                    operation.addTagsItem(apiDefinition.getResourceType());
                    operation.addSecurityItem(generateSecurityScopes(
                            apiDefinition.getResourceType(), "delete"));
                    operation.addExtension("x-auth-type", "Application & Application User");

                    ApiResponses deleteResponses = new ApiResponses();
                    ApiResponse deleteSuccessResponse = new ApiResponse();
                    deleteSuccessResponse.setDescription(
                            interaction.getKey() + " " + apiDefinition.getResourceType() + " operation successful");
                    deleteResponses.addApiResponse("204", deleteSuccessResponse);
                    operation.setResponses(deleteResponses);
                    operation.addParametersItem(OASGenUtils.generateParameter(
                            "id", "logical identifier", "string", "path", true));
                    idPath.setDelete(operation);
                    break;
            }
        }
        paths.addPathItem("/" + apiDefinition.getResourceType(), rootPath);
        paths.addPathItem("/" + apiDefinition.getResourceType() + "/{id}", idPath);
        apiDefinition.getOpenAPI().setPaths(paths);
    }
}
