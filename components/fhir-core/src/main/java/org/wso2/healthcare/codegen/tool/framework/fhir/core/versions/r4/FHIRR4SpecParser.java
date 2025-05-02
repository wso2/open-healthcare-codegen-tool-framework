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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.OperationDefinition;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.SearchParameter;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ImplementationGuide;
import org.hl7.fhir.r4.model.CodeType;
import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.AbstractFHIRSpecParser;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model.APIDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.FHIRToolConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.IGConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common.FHIRR4SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common.FHIRR4SpecUtils;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4DataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4OperationDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4ResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4SearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4TerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.oas.R4OASGenerator;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is responsible for parsing the FHIR R4 specification files.
 */
public class FHIRR4SpecParser extends AbstractFHIRSpecParser {

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

        Map<String, IGConfig> igConfigs = ((FHIRToolConfig) toolConfig).getIgConfigs();

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
                            parsedDef = parseDefinition(CTX, terminologyFile);

                            if (parsedDef instanceof Bundle) {
                                Bundle terminologyBundle = (Bundle) parsedDef;

                                for (Bundle.BundleEntryComponent entryComponent : terminologyBundle.getEntry()) {
                                    String terminologyType = entryComponent.getResource().getResourceType().name();

                                    if (terminologyType.equals("CodeSystem")) {
                                        CodeSystem codeSystem = (CodeSystem) entryComponent.getResource();
                                        FHIRR4TerminologyDef fhirTerminologyDef = new FHIRR4TerminologyDef();
                                        fhirTerminologyDef.setTerminologyResource(codeSystem);
                                        fhirTerminologyDef.setUrl(codeSystem.getUrl());
                                        FHIRR4SpecificationData.getDataHolderInstance().addCodeSystem(fhirTerminologyDef.getUrl(), fhirTerminologyDef);
                                    }
                                    else if (terminologyType.equals("ValueSet")) {
                                        ValueSet valueSet = (ValueSet) entryComponent.getResource();
                                        FHIRR4TerminologyDef fhirTerminologyDef = new FHIRR4TerminologyDef();
                                        fhirTerminologyDef.setTerminologyResource(valueSet);
                                        fhirTerminologyDef.setUrl(valueSet.getUrl());
                                        FHIRR4SpecificationData.getDataHolderInstance().addValueSet(fhirTerminologyDef.getUrl(), fhirTerminologyDef);
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

        List<String> dataTypeProfileDirs = ((FHIRToolConfig) toolConfig).getDataTypeProfileDirs();
        for (String dataTypeProfileDir : dataTypeProfileDirs) {
            File dataTypeProfileDirPath = new File(toolConfig.getSpecBasePath() + dataTypeProfileDir);

            if (dataTypeProfileDirPath.isDirectory()) {
                File[] dataProfileFiles = dataTypeProfileDirPath.listFiles(jsonFileFilter);

                if (dataProfileFiles != null) {
                    for (File dataProfileFile : dataProfileFiles) {
                        IBaseResource parsedDef;

                        try {
                            parsedDef = parseDefinition(CTX, dataProfileFile);

                            if (parsedDef instanceof StructureDefinition) {
                                StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                                String code = structureDefinition.getKind().toCode();

                                if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                    FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                                    dataTypeDef.setDefinition(structureDefinition);
                                    dataTypeDef.setKind(DefKind.fromCode(code));
                                    FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
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
    @Override
    public void parseIG(ToolConfig toolConfig, String igName, String igDirPath) {

        String igPath = igDirPath.contains(toolConfig.getSpecBasePath()) ?
                igDirPath : toolConfig.getSpecBasePath() + igDirPath;
        File igDirPathFile = new File(igPath);

        if (igDirPathFile.isDirectory()) {
            File[] igProfileFiles = igDirPathFile.listFiles(jsonFileFilter);

            if (igProfileFiles != null) {
                FHIRImplementationGuide fhirImplementationGuide = FHIRR4SpecificationData.getDataHolderInstance().getFhirImplementationGuides().get(igName);

                if (fhirImplementationGuide == null) {
                    fhirImplementationGuide = new FHIRImplementationGuide();
                    fhirImplementationGuide.setName(igName);
                    FHIRR4SpecificationData.getDataHolderInstance().addFhirImplementationGuide(igName, fhirImplementationGuide);
                }

                for (File igProfileFile : igProfileFiles) {
                    if (igProfileFile.isDirectory() || !isValidFHIRDefinition(igProfileFile, LOG)) {
                        continue;
                    }

                    IBaseResource parsedDef;
                    try {
                        parsedDef = parseDefinition(CTX, igProfileFile);
                        if (parsedDef instanceof StructureDefinition) {
                            StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                            String code = structureDefinition.getKind().toCode();

                            if ("resource".equals(code)) {
                                FHIRR4ResourceDef fhirR4ResourceDef = new FHIRR4ResourceDef();
                                fhirR4ResourceDef.setDefinition(structureDefinition);
                                fhirR4ResourceDef.setKind(DefKind.fromCode(code));
                                fhirImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(), fhirR4ResourceDef);

                                R4OASGenerator oasGenerator = R4OASGenerator.getInstance();
                                APIDefinition apiDefinition;

                                if (fhirImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())) {
                                    apiDefinition = fhirImplementationGuide.getApiDefinitions().get(structureDefinition.getType());
                                }
                                else {
                                    apiDefinition = new APIDefinition();
                                    apiDefinition.setResourceType(structureDefinition.getType());
                                }
                                apiDefinition.addSupportedProfile(structureDefinition.getUrl());
                                apiDefinition.addSupportedIg(igName);
                                apiDefinition.setOpenAPI(oasGenerator.generateResourceSchema(apiDefinition, structureDefinition));
                                fhirImplementationGuide.addApiDefinition(structureDefinition.getType(), apiDefinition);
                            }
                            else if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                                dataTypeDef.setDefinition(structureDefinition);
                                dataTypeDef.setKind(DefKind.fromCode(code));
                                FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
                            }
                        }
                        else if (parsedDef instanceof SearchParameter) {
                            SearchParameter searchParameter = (SearchParameter) parsedDef;
                            FHIRR4SearchParamDef fhirR4SearchParamDef = new FHIRR4SearchParamDef();
                            fhirR4SearchParamDef.setSearchParameter(searchParameter);
                            fhirImplementationGuide.getSearchParameters().putIfAbsent(searchParameter.getUrl(), fhirR4SearchParamDef);
                        }
                        else if (parsedDef instanceof Bundle) {
                            //Bundled definitions
                            Bundle definitions = (Bundle) parsedDef;
                            for (Bundle.BundleEntryComponent entry : definitions.getEntry()) {
                                Resource fhirResourceEntry = entry.getResource();

                                if (fhirResourceEntry instanceof StructureDefinition) {
                                    // Bundled structure definitions
                                    StructureDefinition structureDefinition = (StructureDefinition) fhirResourceEntry;
                                    String code = structureDefinition.getKind().toCode();

                                    if ("resource".equals(code)) {
                                        FHIRR4ResourceDef fhirR4ResourceDef = new FHIRR4ResourceDef();
                                        fhirR4ResourceDef.setDefinition(structureDefinition);
                                        fhirR4ResourceDef.setKind(DefKind.fromCode(code));
                                        fhirImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(), fhirR4ResourceDef);

                                        R4OASGenerator oasGenerator = R4OASGenerator.getInstance();
                                        APIDefinition apiDefinition;

                                        if (fhirImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())) {
                                            apiDefinition = fhirImplementationGuide.getApiDefinitions().get(structureDefinition.getType());
                                        }
                                        else {
                                            apiDefinition = new APIDefinition();
                                            apiDefinition.setResourceType(structureDefinition.getType());
                                        }
                                        apiDefinition.addSupportedProfile(structureDefinition.getUrl());
                                        apiDefinition.addSupportedIg(igName);
                                        apiDefinition.setOpenAPI(oasGenerator.generateResourceSchema(apiDefinition, structureDefinition));
                                        fhirImplementationGuide.addApiDefinition(structureDefinition.getType(), apiDefinition);
                                    }
                                    else if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                        FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                                        dataTypeDef.setDefinition(structureDefinition);
                                        dataTypeDef.setKind(DefKind.fromCode(code));
                                        FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
                                    }
                                }
                                else if (fhirResourceEntry instanceof SearchParameter) {
                                    //Bundled search parameters
                                    SearchParameter searchParameter = (SearchParameter) fhirResourceEntry;
                                    FHIRR4SearchParamDef fhirR4SearchParamDef = new FHIRR4SearchParamDef();
                                    fhirR4SearchParamDef.setSearchParameter(searchParameter);
                                    fhirImplementationGuide.getSearchParameters().putIfAbsent(searchParameter.getUrl(), fhirR4SearchParamDef);
                                }
                                else if (fhirResourceEntry instanceof OperationDefinition) {
                                    //Bundled Operation Definitions
                                    OperationDefinition operationDefinition = (OperationDefinition) fhirResourceEntry;
                                    FHIRR4OperationDef operationDef = new FHIRR4OperationDef();
                                    operationDef.setOperationDefinition(operationDefinition);
                                    fhirImplementationGuide.getOperations().putIfAbsent(operationDefinition.getUrl(), operationDef);
                                }
                            }
                        }
                        else if (parsedDef instanceof CodeSystem) {
                            CodeSystem codeSystem = (CodeSystem) parsedDef;
                            FHIRR4TerminologyDef fhirR4TerminologyDef = new FHIRR4TerminologyDef();
                            fhirR4TerminologyDef.setTerminologyResource(codeSystem);
                            fhirR4TerminologyDef.setUrl(codeSystem.getUrl());
                            FHIRR4SpecificationData.getDataHolderInstance().addCodeSystem(fhirR4TerminologyDef.getUrl(), fhirR4TerminologyDef);
                        }
                        else if (parsedDef instanceof ValueSet) {
                            ValueSet valueSet = (ValueSet) parsedDef;
                            FHIRTerminologyDef fhirR4TerminologyDef = new FHIRR4TerminologyDef();
                            fhirR4TerminologyDef.setTerminologyResource(valueSet);
                            fhirR4TerminologyDef.setUrl(valueSet.getUrl());
                            FHIRR4SpecificationData.getDataHolderInstance().addValueSet(fhirR4TerminologyDef.getUrl(), fhirR4TerminologyDef);
                        }
                        else if (parsedDef instanceof ImplementationGuide) {
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
     * This method is used to populate the base data types to the FHIRR4SpecificationData.
     */
    @Override
    protected void populateBaseDataTypes() {
        //read base data types from resources
        for (String baseDataTypeFile : FHIRR4SpecUtils.getDefaultBaseDataTypeProfiles()) {
            InputStream resourceAsStream = FHIRR4SpecParser.class.getClassLoader().getResourceAsStream("r4/profiles/base-data-types/" + baseDataTypeFile);

            try {
                IBaseResource parsedDef = parseDefinition(CTX, resourceAsStream);

                if (parsedDef instanceof StructureDefinition) {
                    StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                    String code = structureDefinition.getKind().toCode();

                    if ("primary-type".equals(code) || "complex-type".equals(code)) {
                        FHIRR4DataTypeDef dataTypeDef = new FHIRR4DataTypeDef();
                        dataTypeDef.setDefinition(structureDefinition);
                        dataTypeDef.setKind(DefKind.fromCode(code));
                        FHIRR4SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
                    }
                }
            }
            catch (CodeGenException e) {
                LOG.error("Error occurred while processing FHIR data profile definitions.", e);
            }
        }
    }

    @Override
    protected void populateValues() {

        for (Map.Entry<String, ? extends FHIRImplementationGuide> igEntry :
                FHIRR4SpecificationData.getDataHolderInstance().getFhirImplementationGuides().entrySet()) {

            FHIRImplementationGuide ig = (FHIRImplementationGuide) igEntry.getValue();

            for (Map.Entry<String, FHIRResourceDef> resourceEntry : ig.getResources().entrySet()) {
                FHIRR4ResourceDef resourceDef = (FHIRR4ResourceDef) resourceEntry.getValue();
                setParentResource(resourceDef, FHIRR4SpecificationData.getDataHolderInstance());
            }
        }

    }

    public void setParentResource(FHIRResourceDef <StructureDefinition, Extension, ElementDefinition> fhirResourceDef, FHIRSpecificationData specData) {
        String parent = fhirResourceDef.getDefinition().getBaseDefinition();

        //Canonical URL pattern: http://hl7.org/fhir/<country>/<IG>/StructureDefinition/<resource>
        String igName = parent.substring(parent.indexOf("fhir/") + 4, parent.indexOf("/StructureDefinition"));
        if (!StringUtils.isEmpty(igName)) {
            for (String igKey : specData.getFhirImplementationGuides().keySet()) {
                if (Pattern.compile(Pattern.quote(igKey), Pattern.CASE_INSENSITIVE).matcher(igName.replace("/", "")).find()) {
                    FHIRImplementationGuide implementationGuide = specData.getFhirImplementationGuides().get(igKey);
                    if (implementationGuide != null) {
                        fhirResourceDef.setParentResource((FHIRR4ResourceDef) implementationGuide.getResources().get(parent));
                        break;
                    }
                    else {
                        //todo: https://github.com/wso2-enterprise/open-healthcare/issues/1309
                        LOG.debug("Required implementation guide:" + igName + " is not loaded.");
                    }
                }
            }
        }
        else {
            FHIRImplementationGuide implementationGuide = specData.getFhirImplementationGuides().get("international");
            if (implementationGuide != null) {
                fhirResourceDef.setParentResource((FHIRR4ResourceDef) implementationGuide.getResources().get(parent));
            } else {
                LOG.debug("Required implementation guide:" + igName + " is not loaded.");
            }
        }
    }

    /**
     * Populate International SearchParameters map
     * ref:<a href="https://www.hl7.org/fhir/search-parameters.json">https://www.hl7.org/fhir/search-parameters.json</a>
     *
     * @throws CodeGenException
     */
    @Override
    protected void populateCommonSearchParameters() throws CodeGenException {
        InputStream searchParamsStream = FHIRR4SpecParser.class.getClassLoader().getResourceAsStream("r4/profiles/all-search-parameters.json");
        LOG.info("Loading international search parameters");
        Bundle paramsBundle = (Bundle) parseDefinition(CTX, searchParamsStream);

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