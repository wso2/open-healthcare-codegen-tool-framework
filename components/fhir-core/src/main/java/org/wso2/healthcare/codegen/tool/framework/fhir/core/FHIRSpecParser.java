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

package org.wso2.healthcare.codegen.tool.framework.fhir.core;

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
import org.wso2.healthcare.codegen.tool.framework.commons.core.AbstractSpecParser;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecUtils;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.FHIRToolConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.IGConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRDataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIROperationDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRSearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.OASGenerator;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model.APIDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

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
public class FHIRSpecParser extends AbstractSpecParser {

    private static final FhirContext CTX = FhirContext.forR4();

    private static final Log LOG = LogFactory.getLog(FHIRSpecParser.class);

    // Create a FilenameFilter to filter JSON files
    private static final FilenameFilter jsonFileFilter = (dir, name) -> name.toLowerCase().endsWith(".json");

    @Override
    public void parse(ToolConfig toolConfig) {

        try {
            populateCommonSearchParameters();
        } catch (CodeGenException e) {
            LOG.error("Error occurred while populating search parameters.", e);
        }

        Map<String, IGConfig> igConfigs = ((FHIRToolConfig) toolConfig).getIgConfigs();
        // Create a FilenameFilter to filter JSON files
        FilenameFilter jsonFileFilter = (dir, name) -> name.toLowerCase().endsWith(".json");
        populateBaseDataTypes();
        for (String igName : igConfigs.keySet()) {
            parseIG(toolConfig, igName, igConfigs.get(igName).getDirPath());
        }

        List<String> terminologyDirs = ((FHIRToolConfig) toolConfig).getTerminologyDirs();
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
                                        FHIRTerminologyDef fhirTerminologyDef = new FHIRTerminologyDef();
                                        fhirTerminologyDef.setTerminologyResource(codeSystem);
                                        fhirTerminologyDef.setUrl(codeSystem.getUrl());
                                        FHIRSpecificationData.getDataHolderInstance().addCodeSystem(fhirTerminologyDef.getUrl(),
                                                fhirTerminologyDef);
                                    } else if (terminologyType.equals("ValueSet")) {
                                        ValueSet valueSet = (ValueSet) entryComponent.getResource();
                                        FHIRTerminologyDef fhirTerminologyDef = new FHIRTerminologyDef();
                                        fhirTerminologyDef.setTerminologyResource(valueSet);
                                        fhirTerminologyDef.setUrl(valueSet.getUrl());
                                        FHIRSpecificationData.getDataHolderInstance().addValueSet(fhirTerminologyDef.getUrl(),
                                                fhirTerminologyDef);
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
        FHIRSpecificationData.getDataHolderInstance().setTerminologies();
        List<String> dataTypeProfileDirs = ((FHIRToolConfig) toolConfig).getDataTypeProfileDirs();
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
                                    FHIRDataTypeDef dataTypeDef = new FHIRDataTypeDef();
                                    dataTypeDef.setDefinition(structureDefinition);
                                    dataTypeDef.setKind(DefKind.fromCode(code));
                                    FHIRSpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
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
     * This method is used to populate the values of the FHIRSpecificationData to the toolContext.
     */
    public void parseIG(ToolConfig toolConfig, String igName, String igDirPath) {

        String igPath = igDirPath.contains(toolConfig.getSpecBasePath()) ?
                igDirPath : toolConfig.getSpecBasePath() + igDirPath;
        File igDirPathFile = new File(igPath);
        if (igDirPathFile.isDirectory()) {
            File[] igProfileFiles = igDirPathFile.listFiles(jsonFileFilter);
            if (igProfileFiles != null) {
                FHIRImplementationGuide fhirImplementationGuide =
                        FHIRSpecificationData.getDataHolderInstance().getFhirImplementationGuides().get(igName);
                if (fhirImplementationGuide == null) {
                    fhirImplementationGuide = new FHIRImplementationGuide();
                    fhirImplementationGuide.setName(igName);
                    FHIRSpecificationData.getDataHolderInstance().addFhirImplementationGuide(igName,
                            fhirImplementationGuide);
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
                                FHIRResourceDef fhirResourceDef = new FHIRResourceDef();
                                fhirResourceDef.setDefinition(structureDefinition);
                                fhirResourceDef.setKind(DefKind.fromCode(code));
                                fhirImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(),
                                        fhirResourceDef);
                                OASGenerator oasGenerator = OASGenerator.getInstance();
                                APIDefinition apiDefinition;
                                if (fhirImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())) {
                                    apiDefinition = fhirImplementationGuide.
                                            getApiDefinitions().get(structureDefinition.getType());
                                } else {
                                    apiDefinition = new APIDefinition();
                                    apiDefinition.setResourceType(structureDefinition.getType());
                                }
                                apiDefinition.addSupportedProfile(structureDefinition.getUrl());
                                apiDefinition.addSupportedIg(igName);
                                apiDefinition.setOpenAPI(oasGenerator.generateResourceSchema(apiDefinition,
                                        structureDefinition));
                                fhirImplementationGuide.addApiDefinition(structureDefinition.getType(), apiDefinition);
                            } else if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                FHIRDataTypeDef dataTypeDef = new FHIRDataTypeDef();
                                dataTypeDef.setDefinition(structureDefinition);
                                dataTypeDef.setKind(DefKind.fromCode(code));
                                FHIRSpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
                                        dataTypeDef);
                            }
                        } else if (parsedDef instanceof SearchParameter) {
                            SearchParameter searchParameter = (SearchParameter) parsedDef;
                            FHIRSearchParamDef fhirSearchParamDef = new FHIRSearchParamDef();
                            fhirSearchParamDef.setSearchParameter(searchParameter);
                            fhirImplementationGuide.getSearchParameters().putIfAbsent(searchParameter.getUrl(),
                                    fhirSearchParamDef);
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
                                        FHIRResourceDef fhirResourceDef = new FHIRResourceDef();
                                        fhirResourceDef.setDefinition(structureDefinition);
                                        fhirResourceDef.setKind(DefKind.fromCode(code));
                                        fhirImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(),
                                                fhirResourceDef);
                                        OASGenerator oasGenerator = OASGenerator.getInstance();
                                        APIDefinition apiDefinition;
                                        if (fhirImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())) {
                                            apiDefinition = fhirImplementationGuide.
                                                    getApiDefinitions().get(structureDefinition.getType());
                                        } else {
                                            apiDefinition = new APIDefinition();
                                            apiDefinition.setResourceType(structureDefinition.getType());
                                        }
                                        apiDefinition.addSupportedProfile(structureDefinition.getUrl());
                                        apiDefinition.addSupportedIg(igName);
                                        apiDefinition.setOpenAPI(oasGenerator.generateResourceSchema(apiDefinition,
                                                structureDefinition));
                                        fhirImplementationGuide.addApiDefinition(structureDefinition.getType(), apiDefinition);
                                    } else if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                        FHIRDataTypeDef dataTypeDef = new FHIRDataTypeDef();
                                        dataTypeDef.setDefinition(structureDefinition);
                                        dataTypeDef.setKind(DefKind.fromCode(code));
                                        FHIRSpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
                                                dataTypeDef);
                                    }
                                } else if (fhirResourceEntry instanceof SearchParameter) {
                                    //Bundled search parameters
                                    SearchParameter searchParameter = (SearchParameter) fhirResourceEntry;
                                    FHIRSearchParamDef fhirSearchParamDef = new FHIRSearchParamDef();
                                    fhirSearchParamDef.setSearchParameter(searchParameter);
                                    fhirImplementationGuide.getSearchParameters().putIfAbsent(
                                            searchParameter.getUrl(), fhirSearchParamDef);
                                } else if (fhirResourceEntry instanceof OperationDefinition) {
                                    //Bundled Operation Definitions
                                    OperationDefinition operationDefinition = (OperationDefinition) fhirResourceEntry;
                                    FHIROperationDef operationDef = new FHIROperationDef();
                                    operationDef.setOperationDefinition(operationDefinition);
                                    fhirImplementationGuide.getOperations().putIfAbsent(operationDefinition.getUrl(), operationDef);
                                }
                            }
                        } else if (parsedDef instanceof CodeSystem) {
                            CodeSystem codeSystem = (CodeSystem) parsedDef;
                            FHIRTerminologyDef fhirTerminologyDef = new FHIRTerminologyDef();
                            fhirTerminologyDef.setTerminologyResource(codeSystem);
                            fhirTerminologyDef.setUrl(codeSystem.getUrl());
                            FHIRSpecificationData.getDataHolderInstance().addCodeSystem(fhirTerminologyDef.getUrl(),
                                    fhirTerminologyDef);
                        } else if (parsedDef instanceof ValueSet) {
                            ValueSet valueSet = (ValueSet) parsedDef;
                            FHIRTerminologyDef fhirTerminologyDef = new FHIRTerminologyDef();
                            fhirTerminologyDef.setTerminologyResource(valueSet);
                            fhirTerminologyDef.setUrl(valueSet.getUrl());
                            FHIRSpecificationData.getDataHolderInstance().addValueSet(fhirTerminologyDef.getUrl(),
                                    fhirTerminologyDef);
                        } else if (parsedDef instanceof ImplementationGuide) {
                            // overriding the FHIR implementation guide name if the ImplementationGuide resource json
                            // file is available in the IG directory.
                            fhirImplementationGuide.setName(((ImplementationGuide) parsedDef).getName());
                            fhirImplementationGuide.setId(((ImplementationGuide) parsedDef).getId());
                        }
                    } catch (CodeGenException e) {
                        LOG.error("Error occurred while processing FHIR resource definition.", e);
                    }
                }
            }
        }
    }

    /**
     * This method is used to populate the base data types to the FHIRSpecificationData.
     */
    private static void populateBaseDataTypes() {
        //read base data types from resources
        for (String baseDataTypeFile : FHIRSpecUtils.getDefaultBaseDataTypeProfiles()) {
            InputStream resourceAsStream = FHIRSpecParser.class.getClassLoader().getResourceAsStream(
                    "profiles/base-data-types/" + baseDataTypeFile);
            try {
                IBaseResource parsedDef = parseDefinition(resourceAsStream);
                if (parsedDef instanceof StructureDefinition) {
                    StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                    String code = structureDefinition.getKind().toCode();
                    if ("primary-type".equals(code) || "complex-type".equals(code)) {
                        FHIRDataTypeDef dataTypeDef = new FHIRDataTypeDef();
                        dataTypeDef.setDefinition(structureDefinition);
                        dataTypeDef.setKind(DefKind.fromCode(code));
                        FHIRSpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(),
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

        for (Map.Entry<String, FHIRImplementationGuide> igEntry : FHIRSpecificationData.getDataHolderInstance().getFhirImplementationGuides().entrySet()) {
            for (Map.Entry<String, FHIRResourceDef> resourceEntry : igEntry.getValue().getResources().entrySet()) {
                setParentResource(resourceEntry.getValue(), FHIRSpecificationData.getDataHolderInstance());
            }
        }

    }

    public static void setParentResource(FHIRResourceDef fhirResourceDef, FHIRSpecificationData specData) {
        String parent = fhirResourceDef.getDefinition().getBaseDefinition();
        //Canonical URL pattern: http://hl7.org/fhir/<country>/<IG>/StructureDefinition/<resource>
        String igName = parent.substring(parent.indexOf("fhir/") + 4, parent.indexOf("/StructureDefinition"));
        if (!StringUtils.isEmpty(igName)) {
            for (String igKey : specData.getFhirImplementationGuides().keySet()) {
                if (Pattern.compile(Pattern.quote(igKey), Pattern.CASE_INSENSITIVE).matcher(igName.replace("/", "")).find()) {
                    FHIRImplementationGuide implementationGuide = specData.getFhirImplementationGuides().get(igKey);
                    if (implementationGuide != null) {
                        fhirResourceDef.setParentResource(implementationGuide.getResources().get(parent));
                        break;
                    } else {
                        //todo: https://github.com/wso2-enterprise/open-healthcare/issues/1309
                        LOG.debug("Required implementation guide:" + igName + " is not loaded.");
                    }
                }
            }
        } else {
            FHIRImplementationGuide implementationGuide = specData.getFhirImplementationGuides().get("international");
            if (implementationGuide != null) {
                fhirResourceDef.setParentResource(implementationGuide.getResources().get(parent));
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
        InputStream searchParamsStream = FHIRSpecParser.class.getClassLoader().getResourceAsStream(
                "profiles/all-search-parameters.json");
        LOG.info("Loading international search parameters");
        Bundle paramsBundle = (Bundle) parseDefinition(searchParamsStream);

        for (Bundle.BundleEntryComponent resource : paramsBundle.getEntry()) {
            if (resource.hasResource() && "SearchParameter".equals(resource.getResource().getResourceType().toString())) {
                SearchParameter searchParameter = (SearchParameter) resource.getResource();
                for (CodeType baseResource : searchParameter.getBase()) {
                    String resourceName = baseResource.getCode();
                    FHIRSpecificationData.getDataHolderInstance().addInternationalSearchParameter(resourceName,
                            new FHIRSearchParamDef(searchParameter));
                }
            }
        }
    }
}
