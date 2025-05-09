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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.ImplementationGuide;
import org.hl7.fhir.r5.model.OperationDefinition;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.model.SearchParameter;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.hl7.fhir.r5.model.ValueSet;
import org.hl7.fhir.r5.model.Enumeration;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Enumerations.VersionIndependentResourceTypesAll;
import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.AbstractFHIRSpecParser;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.FHIRToolConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.IGConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRImplementationGuide;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.model.APIDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common.FHIRR5SpecUtils;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common.FHIRR5SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5TerminologyDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5DataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5ResourceDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5SearchParamDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5OperationDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.oas.R5OASGenerator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class is responsible for parsing the FHIR R5 specification files.
 */
public class FHIRR5SpecParser extends AbstractFHIRSpecParser {

    private static final FhirContext CTX = FhirContext.forR5();
    private static final Log LOG = LogFactory.getLog(FHIRR5SpecParser.class);

    // File filter to get JSON files
    private static final FilenameFilter jsonFileFilter = (dir, name) -> name.endsWith(".json") && name.matches("^[a-zA-Z].*");

    @Override
    public void parse(ToolConfig toolConfig){
        try{
            populateCommonSearchParameters();
        }
        catch (CodeGenException e){
            LOG.error("Error occurred while populating search parameters", e);
        }

        Map<String, IGConfig> igConfigs = ((FHIRToolConfig) toolConfig).getIgConfigs();

        populateBaseDataTypes();

        for(String igName : igConfigs.keySet()){
            parseIG(toolConfig, igName, igConfigs.get(igName).getDirPath());
        }

        List<String> terminologyDirs = ((FHIRToolConfig) toolConfig).getTerminologyDirs();
        for(String terminologyDir : terminologyDirs){
            File terminologyDirPath = new File(toolConfig.getSpecBasePath() + terminologyDir);

            if(terminologyDirPath.isDirectory()){
                File[] terminologyFiles = terminologyDirPath.listFiles(jsonFileFilter);

                if(terminologyFiles != null){
                    for(File terminologyFile : terminologyFiles){
                        IBaseResource parsedDef;

                        try{
                            parsedDef = parseDefinition(CTX, terminologyFile);

                            if(parsedDef instanceof Bundle){
                                Bundle terminologyBundle = (Bundle) parsedDef;

                                for(Bundle.BundleEntryComponent entryComponent : terminologyBundle.getEntry()){
                                    String terminologyType = entryComponent.getResource().getResourceType().name();

                                    if (terminologyType.equals("CodeSystem")) {
                                        CodeSystem codeSystem = (CodeSystem) entryComponent.getResource();
                                        FHIRR5TerminologyDef fhirTerminologyDef = new FHIRR5TerminologyDef();
                                        fhirTerminologyDef.setTerminologyResource(codeSystem);
                                        fhirTerminologyDef.setUrl(codeSystem.getUrl());
                                        FHIRR5SpecificationData.getDataHolderInstance().addCodeSystem(fhirTerminologyDef.getUrl(), fhirTerminologyDef);
                                    }
                                    else if(terminologyType.equals("ValueSet")){
                                        ValueSet valueSet = (ValueSet) entryComponent.getResource();
                                        FHIRR5TerminologyDef fhirTerminologyDef = new FHIRR5TerminologyDef();
                                        fhirTerminologyDef.setTerminologyResource(valueSet);
                                        fhirTerminologyDef.setUrl(valueSet.getUrl());
                                        FHIRR5SpecificationData.getDataHolderInstance().addCodeSystem(fhirTerminologyDef.getUrl(), fhirTerminologyDef);
                                    }
                                }
                            }
                        }
                        catch (CodeGenException e){
                            LOG.error("Error occurred while processing FHIR data profile definition.", e);
                        }
                    }
                }
            }
        }
        FHIRR5SpecificationData.getDataHolderInstance().setTerminologies();

        List <String> dataTypeProfileDirs = ((FHIRToolConfig) toolConfig).getDataTypeProfileDirs();
        for(String dataTypeProfileDir : dataTypeProfileDirs){
            File dataTypeProfileDirPath = new File(toolConfig.getSpecBasePath() + dataTypeProfileDir);

            if(dataTypeProfileDirPath.isDirectory()){
                File[] dataProfileFiles = dataTypeProfileDirPath.listFiles(jsonFileFilter);

                if(dataProfileFiles != null){
                    for(File dataProfileFile : dataProfileFiles){
                        IBaseResource parsedDef;

                        try{
                            parsedDef = parseDefinition(CTX, dataProfileFile);

                            if(parsedDef instanceof StructureDefinition){
                                StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                                String code = structureDefinition.getKind().toCode();

                                if("primary-type".equals(code) || "complex-type".equals(code)){
                                    FHIRR5DataTypeDef dataTypeDef = new FHIRR5DataTypeDef();
                                    dataTypeDef.setDefinition(structureDefinition);
                                    dataTypeDef.setKind(DefKind.fromCode(code));
                                    FHIRR5SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
                                }
                            }
                        }
                        catch (CodeGenException e){
                            LOG.error("Error occurred while processing FHIR data profile definition.", e);
                        }
                    }
                }
            }
        }
        populateValues();
    }

    /**
     * This method is used to populate the values of the FHIRR5SpecificationData to the toolContext.
     */
    @Override
    public void parseIG(ToolConfig toolConfig, String igName, String igDirPath){
        String igPath = igDirPath.contains(toolConfig.getSpecBasePath()) ? igDirPath : toolConfig.getSpecBasePath() + igDirPath;
        File igDirPathFile = new File(igPath);

        if(igDirPathFile.isDirectory()){
            File[] igProfileFiles = igDirPathFile.listFiles(jsonFileFilter);

            if(igProfileFiles != null){
                FHIRImplementationGuide fhirImplementationGuide = FHIRR5SpecificationData.getDataHolderInstance().getFhirImplementationGuides().get(igName);

                if(fhirImplementationGuide == null){
                    fhirImplementationGuide = new FHIRImplementationGuide();
                    fhirImplementationGuide.setName(igName);
                    FHIRR5SpecificationData.getDataHolderInstance().addFhirImplementationGuide(igName, fhirImplementationGuide);
                }

                for(File igProfileFile : igProfileFiles){
                    if(igProfileFile.isDirectory() || !isValidFHIRDefinition(igProfileFile, LOG)){
                        continue;
                    }

                    IBaseResource parsedDef;
                    try{
                        parsedDef = parseDefinition(CTX, igProfileFile);

                        if(parsedDef instanceof StructureDefinition){
                            StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                            String code = structureDefinition.getKind().toCode();

                            if(code.equals("resource")){
                                FHIRR5ResourceDef fhirR5ResourceDef = new FHIRR5ResourceDef();
                                fhirR5ResourceDef.setDefinition(structureDefinition);
                                fhirR5ResourceDef.setKind(DefKind.fromCode(code));
                                fhirImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(), fhirR5ResourceDef);

                                R5OASGenerator oasGenerator = R5OASGenerator.getInstance();
                                APIDefinition apiDefinition;

                                if(fhirImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())){
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
                                FHIRR5DataTypeDef dataTypeDef = new FHIRR5DataTypeDef();
                                dataTypeDef.setDefinition(structureDefinition);
                                dataTypeDef.setKind(DefKind.fromCode(code));
                                FHIRR5SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
                            }
                        }
                        else if(parsedDef instanceof SearchParameter){
                            SearchParameter searchParameter = (SearchParameter) parsedDef;
                            FHIRR5SearchParamDef fhirR5SearchParamDef = new FHIRR5SearchParamDef();
                            fhirR5SearchParamDef.setSearchParameter(searchParameter);
                            fhirImplementationGuide.getSearchParameters().putIfAbsent(searchParameter.getUrl(), fhirR5SearchParamDef);
                        }
                        else if(parsedDef instanceof Bundle){
                            Bundle definitions = (Bundle) parsedDef;

                            for(Bundle.BundleEntryComponent entryComponent : definitions.getEntry()){
                                Resource fhirEntryResource = entryComponent.getResource();

                                // Bundled Structure Definitions
                                if(fhirEntryResource instanceof  StructureDefinition){
                                    StructureDefinition structureDefinition = (StructureDefinition) fhirEntryResource;
                                    String code = structureDefinition.getKind().toCode();

                                    if("resource".equals(code)){
                                        FHIRR5ResourceDef fhirR5ResourceDef = new FHIRR5ResourceDef();
                                        fhirR5ResourceDef.setDefinition(structureDefinition);
                                        fhirR5ResourceDef.setKind(DefKind.fromCode(code));
                                        fhirImplementationGuide.getResources().putIfAbsent(structureDefinition.getUrl(), fhirR5ResourceDef);

                                        R5OASGenerator oasGenerator = R5OASGenerator.getInstance();
                                        APIDefinition apiDefinition;

                                        if(fhirImplementationGuide.getApiDefinitions().containsKey(structureDefinition.getType())){
                                            apiDefinition = fhirImplementationGuide.getApiDefinitions().get(structureDefinition.getType());
                                        }
                                        else{
                                            apiDefinition = new APIDefinition();
                                            apiDefinition.setResourceType(structureDefinition.getType());
                                        }
                                        apiDefinition.addSupportedProfile(structureDefinition.getUrl());
                                        apiDefinition.addSupportedIg(igName);
                                        apiDefinition.setOpenAPI(oasGenerator.generateResourceSchema(apiDefinition, structureDefinition));
                                        fhirImplementationGuide.addApiDefinition(structureDefinition.getType(), apiDefinition);
                                    } 
                                    else if ("primary-type".equals(code) || "complex-type".equals(code)) {
                                        FHIRR5DataTypeDef dataTypeDef = new FHIRR5DataTypeDef();
                                        dataTypeDef.setDefinition(structureDefinition);
                                        dataTypeDef.setKind(DefKind.fromCode(code));
                                        FHIRR5SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
                                    }
                                }
                                else if (fhirEntryResource instanceof SearchParameter) {
                                    SearchParameter searchParameter = (SearchParameter) fhirEntryResource;
                                    FHIRR5SearchParamDef fhirr5SearchParamDef = new FHIRR5SearchParamDef();
                                    fhirr5SearchParamDef.setSearchParameter(searchParameter);
                                    fhirImplementationGuide.getSearchParameters().putIfAbsent(searchParameter.getUrl(), fhirr5SearchParamDef);
                                }
                                else if (fhirEntryResource instanceof OperationDefinition) {
                                    OperationDefinition operationDefinition = (OperationDefinition) fhirEntryResource;
                                    FHIRR5OperationDef operationDef = new FHIRR5OperationDef();
                                    operationDef.setOperationDefinition(operationDefinition);
                                    fhirImplementationGuide.getOperations().putIfAbsent(operationDefinition.getUrl(), operationDef);
                                }
                            }
                        }
                        else if(parsedDef instanceof CodeSystem){
                            CodeSystem codeSystem = (CodeSystem) parsedDef;
                            FHIRR5TerminologyDef fhirR5TerminologyDef = new FHIRR5TerminologyDef();
                            fhirR5TerminologyDef.setTerminologyResource(codeSystem);
                            fhirR5TerminologyDef.setUrl(codeSystem.getUrl());
                            FHIRR5SpecificationData.getDataHolderInstance().addCodeSystem(fhirR5TerminologyDef.getUrl(), fhirR5TerminologyDef);
                        }
                        else if(parsedDef instanceof ValueSet){
                            ValueSet valueSet = (ValueSet) parsedDef;
                            FHIRR5TerminologyDef fhirR5TerminologyDef = new FHIRR5TerminologyDef();
                            fhirR5TerminologyDef.setTerminologyResource(valueSet);
                            fhirR5TerminologyDef.setUrl(valueSet.getUrl());
                            FHIRR5SpecificationData.getDataHolderInstance().addValueSet(fhirR5TerminologyDef.getUrl(), fhirR5TerminologyDef);
                        }
                        else if(parsedDef instanceof ImplementationGuide){
                            // overriding the FHIR implementation guide name if the ImplementationGuide resource json
                            // file is available in the IG directory.
                            ImplementationGuide implementationGuide = (ImplementationGuide) parsedDef;
                            fhirImplementationGuide.setName(implementationGuide.getName());
                            fhirImplementationGuide.setId(implementationGuide.getId());
                        }
                    }
                    catch(CodeGenException e){
                        LOG.error("Error occurred while processing FHIR resource definition.", e);
                    }
                }
            }
        }
    }

    /**
     * Populates the common search parameters from the FHIR specification.
     * ref:<a href="https://hl7.org/fhir/R5/search-parameters.json">https://hl7.org/fhir/R5/search-parameters.json</a>
     *
     * @throws CodeGenException if an error occurs while populating search parameters
     */
    @Override
    protected void populateCommonSearchParameters() throws CodeGenException {
        InputStream searchParamStream = FHIRR5SpecParser.class.getClassLoader().getResourceAsStream("r5/profiles/all-search-parameters.json");
        LOG.info("Loading international search parameters");
        Bundle paramsBundle = (Bundle) parseDefinition(CTX, searchParamStream);

        for (Bundle.BundleEntryComponent resource : paramsBundle.getEntry()){
            if(resource.hasResource() && "SearchParameter".equals(resource.getResource().getResourceType().toString())){
                SearchParameter searchParameter = (SearchParameter) resource.getResource();

                for(Enumeration<VersionIndependentResourceTypesAll> baseResource : searchParameter.getBase()){
                    String resourceName = baseResource.getValue().toCode();
                    FHIRR5SpecificationData.getDataHolderInstance().addInternationalSearchParameter(resourceName, new FHIRR5SearchParamDef(searchParameter));
                }
            }
        }
    }

    /**
     * This method is used to populate the base data types to the FHIRR5SpecificationData.
     */
    @Override
    public void populateBaseDataTypes(){
        for(String baseDataTypeFile : FHIRR5SpecUtils.getDefaultBaseDataTypeProfiles()){
            InputStream resourceAsStream = FHIRR5SpecParser.class.getClassLoader().getResourceAsStream("r5/profiles/base-data-types/" + baseDataTypeFile);

            try{
                IBaseResource parsedDef = parseDefinition(CTX, resourceAsStream);

                if(parsedDef instanceof StructureDefinition){
                    StructureDefinition structureDefinition = (StructureDefinition) parsedDef;
                    String code = structureDefinition.getKind().toCode();

                    if("primary-type".equals(code) || "complex-type".equals(code)){
                        FHIRR5DataTypeDef dataTypeDef = new FHIRR5DataTypeDef();
                        dataTypeDef.setDefinition(structureDefinition);
                        dataTypeDef.setKind(DefKind.fromCode(code));
                        FHIRR5SpecificationData.getDataHolderInstance().addDataType(structureDefinition.getId(), dataTypeDef);
                    }
                }
            }
            catch (CodeGenException e){
                LOG.error("Error occurred while processing FHIR data profile definitions.", e);
            }
        }
    }

    @Override
    protected void populateValues(){
        for(Map.Entry<String, ? extends FHIRImplementationGuide> igEntry :
            FHIRR5SpecificationData.getDataHolderInstance().getFhirImplementationGuides().entrySet()){

            FHIRImplementationGuide ig = igEntry.getValue();
            for (Map.Entry<String, FHIRResourceDef> resourceEntry : ig.getResources().entrySet()){
                FHIRR5ResourceDef resourceDef = (FHIRR5ResourceDef) resourceEntry.getValue();
                setParentResource(resourceDef, FHIRR5SpecificationData.getDataHolderInstance());
            }
        }
    }

    private static void setParentResource(FHIRResourceDef<StructureDefinition, Extension, ElementDefinition> fhirResourceDef, FHIRR5SpecificationData specData){
        String parent = fhirResourceDef.getDefinition().getBaseDefinition();

        //Canonical URL pattern: http://hl7.org/fhir/<country>/<IG>/StructureDefinition
        String igName = parent.substring(parent.indexOf("fhir/") + 4, parent.indexOf("/StructureDefinition"));

        if(!StringUtils.isEmpty(igName)){
            for(String igKey : specData.getFhirImplementationGuides().keySet()){
                if (Pattern.compile(Pattern.quote(igKey), Pattern.CASE_INSENSITIVE).matcher(igName.replace("/", "")).find()){
                    FHIRImplementationGuide implementationGuide = specData.getFhirImplementationGuides().get(igKey);
                    if(implementationGuide != null){
                        fhirResourceDef.setParentResource((FHIRR5ResourceDef)implementationGuide.getResources().get(parent));
                        break;
                    }
                    else{
                        LOG.debug("Required implementation guide:" + igName + " is not loaded.");
                    }
                }
            }
        }
        else{
            FHIRImplementationGuide implementationGuide = specData.getFhirImplementationGuides().get("international");
            if(implementationGuide != null){
                fhirResourceDef.setParentResource((FHIRR5ResourceDef)implementationGuide.getResources().get(parent));
            }
            else{
                LOG.debug("Required implementation guide: international is not loaded.");
            }
        }
    }
}
