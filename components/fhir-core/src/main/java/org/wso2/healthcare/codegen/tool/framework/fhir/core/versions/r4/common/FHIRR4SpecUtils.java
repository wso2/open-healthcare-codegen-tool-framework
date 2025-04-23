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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.common;

import org.hl7.fhir.r4.model.*;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecUtils;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4TerminologyDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility functions on FHIR specification to use by FHIR tools.
 */
public class FHIRR4SpecUtils implements FHIRSpecUtils {

    private static final List<String> defaultSkippedProperties = new ArrayList<>();

    private static final String[] DEFAULT_BASE_DATA_TYPE_PROFILES = {
            "Address.profile.json", "DataRequirement.profile.json", "Period.profile.json", "UsageContext.profile.json",
            "integer.profile.json", "Age.profile.json", "Distance.profile.json", "Quantity.profile.json",
            "base64Binary.profile.json", "markdown.profile.json", "Annotation.profile.json", "Dosage.profile.json",
            "Range.profile.json", "boolean.profile.json", "oid.profile.json", "Attachment.profile.json",
            "Duration.profile.json", "Ratio.profile.json", "canonical.profile.json", "positiveInt.profile.json",
            "CodeableConcept.profile.json", "Expression.profile.json", "Reference.profile.json",
            "code.profile.json", "string.profile.json", "Coding.profile.json", "HumanName.profile.json",
            "RelatedArtifact.profile.json", "date.profile.json", "time.profile.json", "ContactDetail.profile.json",
            "Identifier.profile.json", "SampledData.profile.json", "dateTime.profile.json", "unsignedInt.profile.json",
            "ContactPoint.profile.json", "Meta.profile.json", "Signature.profile.json", "decimal.profile.json",
            "uri.profile.json", "Contributor.profile.json", "Money.profile.json", "Timing.profile.json", "id.profile.json",
            "url.profile.json", "Count.profile.json", "ParameterDefinition.profile.json", "TriggerDefinition.profile.json",
            "instant.profile.json", "uuid.profile.json"
    };

    static {
        defaultSkippedProperties.add("implicitRules");
        defaultSkippedProperties.add("contained");
        defaultSkippedProperties.add("modifierExtension");
        defaultSkippedProperties.add("text");
    }

    public static String getTypeCodeOfElementDef(ElementDefinition element) {
        String typeCode = null;
        List<ElementDefinition.TypeRefComponent> typeList = element.getType();
        if (typeList.size() > 0) {
            typeCode = typeList.get(0).getCode();
        }
        return typeCode;
    }

    public static boolean isMultiDataType(ElementDefinition element) {
        List<ElementDefinition.TypeRefComponent> typeList = element.getType();
        return typeList.size() > 1;
    }

    public static boolean canSkip(StructureDefinition profileDefinition, ElementDefinition element) {

        if (profileDefinition.getType().equals(element.getPath())) return true;

        // Cardinality
        String max = element.getMax();
        if ("0".equals(max)) return true;

        String type = getTypeCodeOfElementDef(element);

        if ("Extension".equals(type) && !element.hasSliceName()) return true;

        String internalFPath = element.getPath().substring(profileDefinition.getType().length() + 1);
        return defaultSkippedProperties.contains(internalFPath);
    }

    /**
     * Resolve terminologies from ValueSets/CodeSystems
     *
     * @param valueSets value sets
     * @param codeSystems code systems
     * @return codings map
     */
    public static Map<String, Map<String, Coding>> resolveTerminology(Map<String, FHIRR4TerminologyDef> valueSets, Map<String, FHIRR4TerminologyDef> codeSystems) {
        Map<String, Map<String, Coding>> codingMap = new HashMap<>();
        Map<String, Coding> codings;

        for (Map.Entry<String, FHIRR4TerminologyDef> valueSetEntry : valueSets.entrySet()) {
            codings = new HashMap<>();
            ValueSet valueSet = (ValueSet) valueSetEntry.getValue().getTerminologyResource();
            ValueSet.ValueSetComposeComponent compose = valueSet.getCompose();
            List<ValueSet.ConceptSetComponent> include = compose.getInclude();
            for (ValueSet.ConceptSetComponent conceptSetComponent : include) {
                String system = conceptSetComponent.getSystem();
                List<ValueSet.ConceptReferenceComponent> concepts = conceptSetComponent.getConcept();
                List<String> includedCodes = new ArrayList<>();
                for (ValueSet.ConceptReferenceComponent concept : concepts) {
                    if (concept.getDisplay() != null) {
                        Coding coding = new Coding();
                        coding.setCode(concept.getCode());
                        coding.setDisplay(concept.getDisplay());
                        coding.setSystem(system);
                        codings.put(concept.getCode(), coding);
                    } else {
                        includedCodes.add(concept.getCode());
                        Coding coding = new Coding();
                        coding.setCode(concept.getCode());
                        coding.setSystem(system);
                        codings.put(concept.getCode(), coding);
                    }
                }

                if (codeSystems.get(system) != null) {
                    CodeSystem codeSystem = (CodeSystem) codeSystems.get(system).getTerminologyResource();
                    if (codeSystem != null) {
                        List<CodeSystem.ConceptDefinitionComponent> concept = codeSystem.getConcept();
                        for (CodeSystem.ConceptDefinitionComponent conceptDefinitionComponent : concept) {
                            List<CodeSystem.ConceptPropertyComponent> property = conceptDefinitionComponent.getProperty();
                            // check if the code is deprecated
                            boolean isDeprecated = false;
                            for (CodeSystem.ConceptPropertyComponent conceptPropertyComponent : property) {
                                if ("status".equals(conceptPropertyComponent.getCode()) && "deprecated"
                                        .equals(conceptPropertyComponent.getValue().toString())) {
                                    isDeprecated = true;
                                    break;
                                }
                            }
                            if (!isDeprecated) {
                                if (concepts.size() > 0 && !includedCodes.contains(conceptDefinitionComponent.getCode())) {
                                    continue;
                                }
                                Coding coding = new Coding();
                                coding.setCode(conceptDefinitionComponent.getCode());
                                coding.setDisplay(conceptDefinitionComponent.getDisplay());
                                coding.setSystem(system);
                                codings.put(conceptDefinitionComponent.getCode(), coding);
                            }
                        }
                    }
                }
            }
            codingMap.put(valueSet.getUrl(), codings);
        }
        return codingMap;
    }

    public static String[] getDefaultBaseDataTypeProfiles() {
        return DEFAULT_BASE_DATA_TYPE_PROFILES;
    }
}
