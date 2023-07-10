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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.core.AbstractSpecParser;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.FHIRToolConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.IGConfig;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.*;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FHIRSpecParser extends AbstractSpecParser {

    private static final FhirContext CTX = FhirContext.forR4();

    private static final Log LOG = LogFactory.getLog(FHIRSpecParser.class);

    @Override
    public void parse(ToolConfig toolConfig) {
        Map<String, IGConfig> igConfigs = ((FHIRToolConfig) toolConfig).getIgConfigs();
        for (String igName : igConfigs.keySet()) {
            IGConfig igConfig = igConfigs.get(igName);
            File igDirPath = new File(igConfig.getDirPath());
            if (igDirPath.isDirectory()) {
                File[] igProfileFiles = igDirPath.listFiles();
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
                                    if (fhirResourceEntry instanceof SearchParameter) {
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
                                        fhirImplementationGuide.getOperations().putIfAbsent(operationDefinition.getUrl(),operationDef);
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
                            }
                        } catch (CodeGenException e) {
                            LOG.error("Error occurred while processing FHIR resource definition.", e);
                        }
                    }
                }
            }
        }

        List<String> terminologyDirs = ((FHIRToolConfig) toolConfig).getTerminologyDirs();
        for (String terminologyDir : terminologyDirs) {
            File terminologyDirPath = new File(terminologyDir);
            if (terminologyDirPath.isDirectory()) {
                File[] terminologyFiles = terminologyDirPath.listFiles();
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
        Map<String, Map<String,Coding>> codingMap = FHIRSpecificationData.getDataHolderInstance().getTerminologies();
        List<String> dataTypeProfileDirs = ((FHIRToolConfig) toolConfig).getDataTypeProfileDirs();
        for (String dataTypeProfileDir : dataTypeProfileDirs) {
            File dataTypeProfileDirPath = new File(dataTypeProfileDir);
            if (dataTypeProfileDirPath.isDirectory()) {
                File[] dataProfileFiles = dataTypeProfileDirPath.listFiles();
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

    public static IBaseResource parseDefinition(File file) throws CodeGenException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new CodeGenException("Error occurred while parsing FHIR definition.", e);
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
                        LOG.error("Required implementation guide:" + igName + " is not loaded.");
                    }
                }
            }
        } else {
            FHIRImplementationGuide implementationGuide = specData.getFhirImplementationGuides().get("international");
            if (implementationGuide != null) {
                fhirResourceDef.setParentResource(implementationGuide.getResources().get(parent));
            } else {
                LOG.error("Required implementation guide:" + igName + " is not loaded.");
            }
        }
    }
}
