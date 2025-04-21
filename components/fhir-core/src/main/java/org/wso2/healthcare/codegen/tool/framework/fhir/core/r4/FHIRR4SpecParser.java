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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.r4;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.ImplementationGuide;
import org.hl7.fhir.r4.model.OperationDefinition;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.SearchParameter;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.ValueSet;
import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.FHIRSpecParser;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.common.FHIRR4SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.common.FHIRR4SpecUtils;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.config.R4FHIRToolConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.config.R4IGConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.model.FHIRR4DataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.model.FHIRR4ImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.model.FHIRR4OperationDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.model.FHIRR4ResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.model.FHIRR4SearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.model.FHIRR4TerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.oas.R4OASGenerator;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.oas.model.R4APIDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.util.R4DefKind;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is responsible for parsing the FHIR specification files.
 */
public class FHIRR4SpecParser extends FHIRSpecParser {

    private static final FhirContext CTX = FhirContext.forR4();

    private static final Log LOG = LogFactory.getLog(FHIRR4SpecParser.class);

    // Create a FilenameFilter to filter JSON files
    private static final FilenameFilter jsonFileFilter = (dir, name) -> name.toLowerCase().endsWith(".json");

    @Override
    public void parse(ToolConfig toolConfig) {

        try {
            populateCommonSearchParameters();
        } catch (CodeGenException e) {
            LOG.error("Error occurred while populating search parameters.", e);
        }

        Map<String, R4IGConfig> igConfigs = ((R4FHIRToolConfig) toolConfig).getIgConfigs();
        // Create a FilenameFilter to filter JSON files
        FilenameFilter jsonFileFilter = (dir, name) -> name.toLowerCase().endsWith(".json");
        populateBaseDataTypes();
        for (String igName : igConfigs.keySet()) {
            parseIG(toolConfig, igName, igConfigs.get(igName).getDirPath());
        }

        List<String> terminologyDirs = ((R4FHIRToolConfig) toolConfig).getTerminologyDirs();
        for (String terminologyDir : terminologyDirs) {
            File terminologyDirPath = new File(toolConfig.getSpecBasePath() + terminologyDir);
            if (terminologyDirPath.isDirectory()) {
                File[] terminologyFiles = terminologyDirPath.listFiles(jsonFileFilter);
                if (terminologyFiles != null) {
                    for (File terminologyFile : terminologyFiles) {
                        IBaseResource parsedDef;
                        try {
                            parsedDef = parseDefinition(terminologyFile);
                            if (parsedDef instanceof Bundle) {
                                Bundle terminologyBundle = (Bundle) parsedDef;
                                for (Bundle.BundleEntryComponent entryComponent : terminologyBundle.getEntry()) {
                                    String terminologyType = entryComponent.getResource().getResourceType().name();
                                    if (terminologyType.equals("CodeSystem")) {
                                        CodeSystem codeSystem = (CodeSystem) entryComponent.getResource();
                                        FHIRR4TerminologyDef fhirTerminologyDef = new FHIRR4TerminologyDef();
                                        fhirTerminologyDef.setTerminologyResource(codeSystem);
                                        fhirTerminologyDef.setUrl(codeSystem.getUrl());
                                        FHIRR4SpecificationData.getDataHolderInstance().addCodeSystem(fhirTerminologyDef.getUrl(),
                                                fhirTerminologyDef);
                                    } else if (terminologyType.equals("ValueSet")) {
                                        ValueSet valueSet = (ValueSet) entryComponent.getResource();
                                        FHIRR4TerminologyDef FHIRR4TerminologyDef = new FHIRR4TerminologyDef();
                                        FHIRR4TerminologyDef.setTerminologyResource(valueSet);
                                        FHIRR4TerminologyDef.setUrl(valueSet.getUrl());
                                        FHIRR4SpecificationData.getDataHolderInstance().addValueSet(FHIRR4TerminologyDef.getUrl(),
                                                FHIRR4TerminologyDef);
                                    }
                                }
                            }
                        } catch (CodeGenException e) {
                            LOG.error("Error occurred while processing FHIR data profile definition.", e);
                        }
                    }
                }
            }
        }

        FHIRR4SpecificationData.getDataHolderInstance().setTerminologies();
        List<String> dataTypeProfileDirs = ((R4FHIRToolConfig) toolConfig).getDataTypeProfileDirs();
        for (String dataTypeProfileDir : dataTypeProfileDirs) {
            File dataTypeProfileDirPath = new File(toolConfig.getSpecBasePath() + dataTypeProfileDir);
            if (dataTypeProfileDirPath.isDirectory()) {
                File[] dataProfileFiles = dataTypeProfileDirPath.listFiles(jsonFileFilter);
                if (dataProfileFiles != null) {
                    for (File dataProfileFile : dataProfileFiles) {
                        IBaseResource parsedDef;
                        try {
                            parsedDef = parseDefinition(dataProfileFile);
                            if (parsedDef instanceof StructureDefinition) {
                                StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                                String code = structureDefinition.getKind().toCode();
                                if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                    FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                                    dataTypeDef.setDefinition(structureDefinition);
                                    dataTypeDef.setKind(R4DefKind.fromCode(code));
                                    FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
                                            dataTypeDef);
                                }
                            }
                        } catch (CodeGenException e) {
                            LOG.error("Error occurred while processing FHIR data profile definition.", e);
                        }
                    }
                }
            }
        }
        populateValues();
    }

    /**
     * This method is used to populate the values of the FHIRR4SpecificationData to the toolContext.
     */
    public void parseIG(ToolConfig toolConfig, String igName, String igDirPath) {

        String igPath = igDirPath.contains(toolConfig.getSpecBasePath()) ?
                igDirPath : toolConfig.getSpecBasePath() + igDirPath;
        File igDirPathFile = new File(igPath);
        if (igDirPathFile.isDirectory()) {
            File[] igProfileFiles = igDirPathFile.listFiles(jsonFileFilter);
            if (igProfileFiles != null) {
                FHIRR4ImplementationGuide FHIRR4ImplementationGuide =
                        (FHIRR4ImplementationGuide) FHIRR4SpecificationData.getDataHolderInstance().getFhirImplementationGuides().get(igName);
                if (FHIRR4ImplementationGuide == null) {
                    FHIRR4ImplementationGuide = new FHIRR4ImplementationGuide();
                    FHIRR4ImplementationGuide.setName(igName);
                    FHIRR4SpecificationData.getDataHolderInstance().addFhirImplementationGuide(igName,
                            FHIRR4ImplementationGuide);
                }
                for (File igProfileFile : igProfileFiles) {
                    if (igProfileFile.isDirectory() || !isValidFHIRDefinition(igProfileFile)) {
                        continue;
                    }
                    IBaseResource parsedDef;
                    try {
                        parsedDef = parseDefinition(igProfileFile);
                        if (parsedDef instanceof StructureDefinition) {
                            StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                            String code = structureDefinition.getKind().toCode();
                            if ("resource".equals(code)) {
                                FHIRR4ResourceDef FHIRR4ResourceDef = new FHIRR4ResourceDef();
                                FHIRR4ResourceDef.setDefinition(structureDefinition);
                                FHIRR4ResourceDef.setKind(R4DefKind.fromCode(code));
                                FHIRR4ImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(),
                                        FHIRR4ResourceDef);
                                R4OASGenerator oasGenerator = R4OASGenerator.getInstance();
                                R4APIDefinition apiDefinition;
                                if (FHIRR4ImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())) {
                                    apiDefinition = FHIRR4ImplementationGuide.
                                            getApiDefinitions().get(structureDefinition.getType());
                                } else {
                                    apiDefinition = new R4APIDefinition();
                                    apiDefinition.setResourceType(structureDefinition.getType());
                                }
                                apiDefinition.addSupportedProfile(structureDefinition.getUrl());
                                apiDefinition.addSupportedIg(igName);
                                apiDefinition.setOpenAPI(oasGenerator.generateResourceSchema(apiDefinition,
                                        structureDefinition));
                                FHIRR4ImplementationGuide.addApiDefinition(structureDefinition.getType(), apiDefinition);
                            } else if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                                dataTypeDef.setDefinition(structureDefinition);
                                dataTypeDef.setKind(R4DefKind.fromCode(code));
                                FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
                                        dataTypeDef);
                            }
                        } else if (parsedDef instanceof SearchParameter) {
                            SearchParameter searchParameter = (SearchParameter) parsedDef;
                            FHIRR4SearchParamDef FHIRR4SearchParamDef = new FHIRR4SearchParamDef();
                            FHIRR4SearchParamDef.setSearchParameter(searchParameter);
                            FHIRR4ImplementationGuide.getSearchParameters().putIfAbsent(searchParameter.getUrl(),
                                    FHIRR4SearchParamDef);
                        } else if (parsedDef instanceof Bundle) {
                            //Bundled definitions
                            Bundle definitions = (Bundle) parsedDef;
                            for (Bundle.BundleEntryComponent entry : definitions.getEntry()) {
                                Resource fhirResourceEntry = entry.getResource();
                                if (fhirResourceEntry instanceof StructureDefinition) {
                                    // Bundled structure definitions
                                    StructureDefinition structureDefinition = (StructureDefinition) fhirResourceEntry;
                                    String code = structureDefinition.getKind().toCode();
                                    if ("resource".equals(code)) {
                                        FHIRR4ResourceDef FHIRR4ResourceDef = new FHIRR4ResourceDef();
                                        FHIRR4ResourceDef.setDefinition(structureDefinition);
                                        FHIRR4ResourceDef.setKind(R4DefKind.fromCode(code));
                                        FHIRR4ImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(),
                                                FHIRR4ResourceDef);
                                        R4OASGenerator oasGenerator = R4OASGenerator.getInstance();
                                        R4APIDefinition apiDefinition;
                                        if (FHIRR4ImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())) {
                                            apiDefinition = FHIRR4ImplementationGuide.
                                                    getApiDefinitions().get(structureDefinition.getType());
                                        } else {
                                            apiDefinition = new R4APIDefinition();
                                            apiDefinition.setResourceType(structureDefinition.getType());
                                        }
                                        apiDefinition.addSupportedProfile(structureDefinition.getUrl());
                                        apiDefinition.addSupportedIg(igName);
                                        apiDefinition.setOpenAPI(oasGenerator.generateResourceSchema(apiDefinition,
                                                structureDefinition));
                                        FHIRR4ImplementationGuide.addApiDefinition(structureDefinition.getType(), apiDefinition);
                                    } else if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                        FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                                        dataTypeDef.setDefinition(structureDefinition);
                                        dataTypeDef.setKind(R4DefKind.fromCode(code));
                                        FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
                                                dataTypeDef);
                                    }
                                } else if (fhirResourceEntry instanceof SearchParameter) {
                                    //Bundled search parameters
                                    SearchParameter searchParameter = (SearchParameter) fhirResourceEntry;
                                    FHIRR4SearchParamDef FHIRR4SearchParamDef = new FHIRR4SearchParamDef();
                                    FHIRR4SearchParamDef.setSearchParameter(searchParameter);
                                    FHIRR4ImplementationGuide.getSearchParameters().putIfAbsent(
                                            searchParameter.getUrl(), FHIRR4SearchParamDef);
                                } else if (fhirResourceEntry instanceof OperationDefinition) {
                                    //Bundled Operation Definitions
                                    OperationDefinition operationDefinition = (OperationDefinition) fhirResourceEntry;
                                    FHIRR4OperationDef operationDef = new FHIRR4OperationDef();
                                    operationDef.setOperationDefinition(operationDefinition);
                                    FHIRR4ImplementationGuide.getOperations().putIfAbsent(operationDefinition.getUrl(), operationDef);
                                }
                            }
                        } else if (parsedDef instanceof CodeSystem) {
                            CodeSystem codeSystem = (CodeSystem) parsedDef;
                            FHIRR4TerminologyDef FHIRR4TerminologyDef = new FHIRR4TerminologyDef();
                            FHIRR4TerminologyDef.setTerminologyResource(codeSystem);
                            FHIRR4TerminologyDef.setUrl(codeSystem.getUrl());
                            FHIRR4SpecificationData.getDataHolderInstance().addCodeSystem(FHIRR4TerminologyDef.getUrl(),
                                    FHIRR4TerminologyDef);
                        } else if (parsedDef instanceof ValueSet) {
                            ValueSet valueSet = (ValueSet) parsedDef;
                            FHIRR4TerminologyDef FHIRR4TerminologyDef = new FHIRR4TerminologyDef();
                            FHIRR4TerminologyDef.setTerminologyResource(valueSet);
                            FHIRR4TerminologyDef.setUrl(valueSet.getUrl());
                            FHIRR4SpecificationData.getDataHolderInstance().addValueSet(FHIRR4TerminologyDef.getUrl(),
                                    FHIRR4TerminologyDef);
                        } else if (parsedDef instanceof ImplementationGuide) {
                            // overriding the FHIR implementation guide name if the ImplementationGuide resource json
                            // file is available in the IG directory.
                            FHIRR4ImplementationGuide.setName(((ImplementationGuide) parsedDef).getName());
                            FHIRR4ImplementationGuide.setId(((ImplementationGuide) parsedDef).getId());
                        }
                    } catch (CodeGenException e) {
                        LOG.error("Error occurred while processing FHIR resource definition.", e);
                    }
                }
            }
        }
    }

    /**
     * This method is used to populate the base data types to the FHIRR4SpecificationData.
     */
    private static void populateBaseDataTypes() {
        //read base data types from resources
        for (String baseDataTypeFile : FHIRR4SpecUtils.getDefaultBaseDataTypeProfiles()) {
            InputStream resourceAsStream = FHIRR4SpecParser.class.getClassLoader().getResourceAsStream(
                    "profiles/base-data-types/" + baseDataTypeFile);
            try {
                IBaseResource parsedDef = parseDefinition(resourceAsStream);
                if (parsedDef instanceof StructureDefinition) {
                    StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                    String code = structureDefinition.getKind().toCode();
                    if ("primary-type".equals(code) || "complex-type".equals(code)) {
                        FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                        dataTypeDef.setDefinition(structureDefinition);
                        dataTypeDef.setKind(R4DefKind.fromCode(code));
                        FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
                                dataTypeDef);
                    }
                }
            } catch (CodeGenException e) {
                LOG.error("Error occurred while processing FHIR data profile definitions.", e);
            }
        }
    }

    /**
     * This method is used to parse the FHIR structure definition from the file.
     *
     * @param file definition file
     * @return parsed FHIR structure definition
     * @throws CodeGenException if an error occurs while parsing the definition
     */
    public static IBaseResource parseDefinition(File file) throws CodeGenException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new CodeGenException("Error occurred while parsing FHIR definition.", e);
        }
    }

    /**
     * This method is used to parse the FHIR structure definition from the input stream.
     *
     * @param inputStream definition file input stream
     * @return parsed FHIR structure definition
     * @throws CodeGenException if an error occurs while parsing the definition
     */
    public static IBaseResource parseDefinition(InputStream inputStream) throws CodeGenException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(inputStream);
        } catch (Exception e) {
            throw new CodeGenException("Error occurred while parsing FHIR definition from the stream.", e);
        }
    }

    /**
     * This method is used to parse the FHIR structure definition from the string.
     *
     * @param resourceContent FHIR resource string
     * @return parsed FHIR structure definition
     * @throws CodeGenException if an error occurs while parsing the definition
     */
    public static IBaseResource parseDefinition(String resourceContent) throws CodeGenException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(resourceContent);
        } catch (Exception e) {
            throw new CodeGenException("Error occurred while parsing FHIR definition from the string.", e);
        }
    }

    public static void populateValues() {

        for (Map.Entry<String, ? extends FHIRImplementationGuide> igEntry :
                FHIRR4SpecificationData.getDataHolderInstance().getFhirImplementationGuides().entrySet()) {

            FHIRR4ImplementationGuide ig = (FHIRR4ImplementationGuide) igEntry.getValue();

            for (Map.Entry<String, FHIRResourceDef> resourceEntry : ig.getResources().entrySet()) {
                FHIRR4ResourceDef resourceDef = (FHIRR4ResourceDef) resourceEntry.getValue();
                setParentResource(resourceDef, FHIRR4SpecificationData.getDataHolderInstance());
            }
        }

    }

    public static void setParentResource(FHIRR4ResourceDef FHIRR4ResourceDef, FHIRR4SpecificationData specData) {
        String parent = FHIRR4ResourceDef.getDefinition().getBaseDefinition();
        //Canonical URL pattern: http://hl7.org/fhir/<country>/<IG>/StructureDefinition/<resource>
        String igName = parent.substring(parent.indexOf("fhir/") + 4, parent.indexOf("/StructureDefinition"));
        if (!StringUtils.isEmpty(igName)) {
            for (String igKey : specData.getFhirImplementationGuides().keySet()) {
                if (Pattern.compile(Pattern.quote(igKey), Pattern.CASE_INSENSITIVE).matcher(igName.replace("/", "")).find()) {
                    FHIRR4ImplementationGuide implementationGuide = (FHIRR4ImplementationGuide) specData.getFhirImplementationGuides().get(igKey);
                    if (implementationGuide != null) {
                        FHIRR4ResourceDef.setParentResource((FHIRR4ResourceDef) implementationGuide.getResources().get(parent));
                        break;
                    } else {
                        //todo: https://github.com/wso2-enterprise/open-healthcare/issues/1309
                        LOG.debug("Required implementation guide:" + igName + " is not loaded.");
                    }
                }
            }
        } else {
            FHIRR4ImplementationGuide implementationGuide = (FHIRR4ImplementationGuide) specData.getFhirImplementationGuides().get("international");
            if (implementationGuide != null) {
                FHIRR4ResourceDef.setParentResource((FHIRR4ResourceDef) implementationGuide.getResources().get(parent));
            } else {
                LOG.debug("Required implementation guide:" + igName + " is not loaded.");
            }
        }
    }

    /**
     * This method will check whether the given file is a valid FHIR definition file.
     *
     * @param definitionFile definition file
     * @return true if the file is a valid FHIR definition file, false otherwise
     */
    private boolean isValidFHIRDefinition(File definitionFile) {
        try (FileReader fileReader = new FileReader(definitionFile)) {
            JsonElement jsonElement = new Gson().fromJson(fileReader, JsonElement.class);
            if (!jsonElement.isJsonArray()) {
                return jsonElement.getAsJsonObject().has("resourceType");
            }
        } catch (IOException e) {
            LOG.error("Error occurred while reading the definition file: " + definitionFile.getName(), e);
        }
        return false;
    }

    /**
     * Populate International SearchParameters map
     * ref:<a href="https://www.hl7.org/fhir/search-parameters.json">https://www.hl7.org/fhir/search-parameters.json</a>
     *
     * @throws CodeGenException
     */
    private void populateCommonSearchParameters() throws CodeGenException {
        InputStream searchParamsStream = FHIRR4SpecParser.class.getClassLoader().getResourceAsStream(
                "profiles/all-search-parameters.json");
        LOG.info("Loading international search parameters");
        Bundle paramsBundle = (Bundle) parseDefinition(searchParamsStream);

        for (Bundle.BundleEntryComponent resource : paramsBundle.getEntry()) {
            if (resource.hasResource() && "SearchParameter".equals(resource.getResource().getResourceType().toString())) {
                SearchParameter searchParameter = (SearchParameter) resource.getResource();
                for (CodeType baseResource : searchParameter.getBase()) {
                    String resourceName = baseResource.getCode();
                    FHIRR4SpecificationData.getDataHolderInstance().addInternationalSearchParameter(resourceName,
                            new FHIRR4SearchParamDef(searchParameter));
                }
            }
        }
    }
}