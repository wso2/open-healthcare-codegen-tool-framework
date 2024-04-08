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
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.CaseUtils;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.FHIRTool;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model.APIDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class generates OAS definitions for FHIR resources.
 */
public class OASGenerator {

    private static final Log LOG = LogFactory.getLog(OASGenerator.class);

    private static final OASGenerator OAS_GENERATOR_INSTANCE = new OASGenerator();

    private OpenAPI fhirOASBaseStructure;

    private OASGenerator() {
        try {
            populateFhirOASBaseStructure();
        } catch (IOException e) {
            LOG.error("Error occurred while getting the base OAS structure.", e);
        }
    }

    public static OASGenerator getInstance() {
        return OAS_GENERATOR_INSTANCE;
    }

    public OpenAPI getFhirOASBaseStructure() {
        return fhirOASBaseStructure;
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

    /**
     * Generates OAS definition for a given FHIR resource definition.
     *
     * @param apiDefinition       API definition object
     * @param structureDefinition FHIR resource definition
     * @return Generated OAS definition
     */
    public OpenAPI generateResourceSchema(APIDefinition apiDefinition, StructureDefinition structureDefinition)
            throws CodeGenException {
        OpenAPI resourceOAS = new OpenAPI();
        resourceOAS.setComponents(fhirOASBaseStructure.getComponents());
        apiDefinition.setOpenAPI(resourceOAS);
        populateOASPaths(apiDefinition);
        populateOASInfo(apiDefinition);
        populateOASInternalValues(apiDefinition);
        for (ElementDefinition element : structureDefinition.getSnapshot().getElement()) {
            try {
                String id = element.getId();
                ObjectSchema objectSchema = new ObjectSchema();
                Set<String> requiredElementsCollector = new LinkedHashSet<>();
                ComposedSchema allOfSchema = new ComposedSchema();
                Map<String, Schema> propertySchemaMap = new HashMap<>();
                Schema parentReference = new Schema();
                parentReference.$ref(APIDefinitionConstants.OAS_REF_SCHEMAS + "DomainResource");
                allOfSchema.addAllOfItem(parentReference);
                for (ElementDefinition.TypeRefComponent type : element.getType()) {
                    StringBuilder elementName;
                    // Skip processing the element if the element is not immediate child or a sliced element
                    if (id.codePoints().filter(ch -> ch == (int)'.').count() > 1 || id.contains(":")) {
                        //sliced element. skip for now, todo: handle separately
                        continue;
                    }
                    if (id.contains("[")) {
                        elementName = new StringBuilder(id.substring(id.lastIndexOf(".") + 1, id.lastIndexOf("[")));
                    } else {
                        elementName = new StringBuilder(id.substring(id.lastIndexOf(".") + 1));
                    }
                    if (element.getType().size() != 1) {
                        elementName.append(CaseUtils.toCamelCase(type.getCode(), true, (char[]) null));
                    }
                    ObjectSchema propertySchema = new ObjectSchema();
                    if (resourceOAS.getComponents().getSchemas().containsKey(type.getCode())) {
                        //schema object available, add ref
                        propertySchema.$ref(APIDefinitionConstants.OAS_REF_SCHEMAS + type.getCode());
                    } else if (APIDefinitionConstants.DATA_TYPE_BACKBONE.equals(type.getCode())) {
                        propertySchema.$ref(APIDefinitionConstants.OAS_REF_SCHEMAS + elementName);
                    } else {
                        String oasDataType = OASGenUtils.mapToOASDataType(
                                type.getCode().substring(type.getCode().lastIndexOf(".") + 1));
                        propertySchema.setType(oasDataType);
                        propertySchema.setPattern(OASGenUtils.getRegexForDataType(oasDataType));
                    }
                    propertySchema.setDescription(element.getDefinition());
                    propertySchemaMap.put(elementName.toString(), propertySchema);
                    if (element.getMin() != 0) {
                        requiredElementsCollector.add(elementName.toString());
                    }
                }
                objectSchema.setProperties(propertySchemaMap);
                allOfSchema.addAllOfItem(objectSchema);
                List<String> requiredElements = new ArrayList<>(requiredElementsCollector);
                allOfSchema.setRequired(requiredElements);
                resourceOAS.getComponents().addSchemas(structureDefinition.getType(), allOfSchema);
            } catch (Exception e) {
                throw new CodeGenException("Error occurred while generating OAS definition for structure definition: "
                        + structureDefinition.getType() + "element: " + element.getId(), e);
            }
        }
        return resourceOAS;
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
    private void populateOASPaths(APIDefinition apiDefinition) {

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
                    readSuccessResponse.setDescription(
                            interaction.getKey() + " " + apiDefinition.getResourceType() + " operation successful");
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
                    operation.addParametersItem(OASGenUtils.generateParameter(
                            "id", "logical identifier", "string", "path", true));
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

    /**
     * Populates internal values of the OAS definition.
     *
     * @param apiDefinition API definition object
     */
    private void populateOASInternalValues(APIDefinition apiDefinition) {
        apiDefinition.getOpenAPI().getInfo().setDescription(OASGenUtils.generateDescription(
                apiDefinition.getResourceType(), apiDefinition.getSupportedProfiles()));
        Tag tag = new Tag();
        tag.setName(apiDefinition.getResourceType());
        Tag fhirVersionTag = new Tag();
        fhirVersionTag.setName(APIDefinitionConstants.FHIR_VERSION);
        for (String igName : apiDefinition.getSupportedProfiles()) {
            Tag igTag = new Tag();
            igTag.setName(igName);
            apiDefinition.getOpenAPI().addTagsItem(igTag);
        }
        apiDefinition.getOpenAPI().addTagsItem(tag);
        apiDefinition.getOpenAPI().addTagsItem(fhirVersionTag);

        Map<String, Object> extensions = new HashMap<>();
        extensions.put(APIDefinitionConstants.OAS_EXTENSION_OH_FHIR_RESOURCE_TYPE, apiDefinition.getResourceType());
        extensions.put(APIDefinitionConstants.OAS_EXTENSION_OH_FHIR_PROFILE, apiDefinition.getSupportedProfiles());
        apiDefinition.getOpenAPI().setExtensions(extensions);

        if (apiDefinition.getOpenAPI().getPaths().get("/") != null) {
            Operation rootGet = apiDefinition.getOpenAPI().getPaths().get("/").getGet();
            if (rootGet != null) {
                for (FHIRSearchParamDef searchParamDef : FHIRSpecificationData.getDataHolderInstance().getInternationalSearchParameters(
                        apiDefinition.getResourceType())) {
                    if (!OASGenUtils.isAdded(searchParamDef.getSearchParameter(), rootGet)) {
                        rootGet.addParametersItem(OASGenUtils.generateParameter(
                                searchParamDef.getSearchParameter().getCode(), searchParamDef.getSearchParameter().getDescription(),
                                searchParamDef.getSearchParameter().getType().toCode(), "query", false));
                    }
                }
                for (String commonParam : apiDefinition.getOpenAPI().getComponents().getParameters().keySet()) {
                    rootGet.addParametersItem(new Parameter().$ref(APIDefinitionConstants.OAS_REF_PARAMETERS + commonParam));
                }
            }
        }
    }
}
