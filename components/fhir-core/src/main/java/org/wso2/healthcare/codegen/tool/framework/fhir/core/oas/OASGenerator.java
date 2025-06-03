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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.FHIRTool;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model.APIDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.OASGenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                    operation.addSecurityItem(new SecurityRequirement().addList("default", new ArrayList<>()));
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
                    operation.addSecurityItem(new SecurityRequirement().addList("default", new ArrayList<>()));
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
                    operation.addSecurityItem(new SecurityRequirement().addList("default", new ArrayList<>()));
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
                    operation.addSecurityItem(new SecurityRequirement().addList("default", new ArrayList<>()));
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
                    operation.addSecurityItem(new SecurityRequirement().addList("default", new ArrayList<>()));
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
                    operation.addSecurityItem(new SecurityRequirement().addList("default", new ArrayList<>()));
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
        paths.addPathItem("/", rootPath);
        paths.addPathItem("/{id}", idPath);
        apiDefinition.getOpenAPI().setPaths(paths);
    }
}
