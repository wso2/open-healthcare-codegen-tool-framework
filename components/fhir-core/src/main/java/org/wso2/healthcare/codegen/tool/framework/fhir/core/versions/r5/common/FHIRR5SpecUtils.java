package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common;

import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.ValueSet;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FHIRR5SpecUtils {
    private static final List<String> defaultSkippedProperties = new ArrayList<>();

    private static final String[] DEFAULT_BASE_DATA_TYPE_PROFILES = {
        "instant.profile.json", "time.profile.json", "date.profile.json", "dateTime.profile.json", "base64Binary.profile.json",
        "boolean.profile.json", "uri.profile.json", "url.profile.json", "canonical.profile.json", "oid.profile.json",
        "uuid.profile.json", "integer.profile.json", "unsignedInt.profile.json", "positiveInt.profile.json",
        "string.profile.json", "code.profile.json", "markdown.profile.json", "id.profile.json", "integer64.profile.json",
        "decimal.profile.json", "Ratio.profile.json", "Period.profile.json", "Range.profile.json", "RatioRange.profile.json",
        "Attachment.profile.json", "Identifier.profile.json", "HumanName.profile.json", "ContactPoint.profile.json",
        "Address.profile.json", "Quantity.profile.json", "Age.profile.json", "Distance.profile.json", "Duration.profile.json",
        "Count.profile.json", "MoneyQuantity.profile.json", "SimpleQuantity.profile.json", "SampledData.profile.json",
        "Signature.profile.json", "BackboneType.profile.json", "Timing.profile.json", "Money.profile.json",
        "Coding.profile.json", "CodeableConcept.profile.json", "Annotation.profile.json", "ContactDetail.profile.json",
        "Contributor.profile.json", "DataRequirement.profile.json", "TriggerDefinition.profile.json",
        "ExtendedContactDetail.profile.json", "UsageContext.profile.json", "VirtualServiceDetail.profile.json",
        "MonetaryComponent.profile.json", "Expression.profile.json", "Availability.profile.json",
        "ParameterDefinition.profile.json", "RelatedArtifact.profile.json", "CodeableReference.profile.json",
        "Meta.profile.json", "Reference.profile.json", "Dosage.profile.json", "ElementDefinition.profile.json",
        "Extension.profile.json", "Narrative.profile.json", "xhtml.profile.json"
    };

    static {
        defaultSkippedProperties.add("implicitRules");
        defaultSkippedProperties.add("contained");
        defaultSkippedProperties.add("modifierExtension");
        defaultSkippedProperties.add("text");
    }

    public static String[] getDefaultBaseDataTypeProfiles(){
        return DEFAULT_BASE_DATA_TYPE_PROFILES;
    }

    public static Map<String, Map<String, Coding>> resolveTerminology(Map<String, FHIRTerminologyDef> valueSets, Map<String, FHIRTerminologyDef> codeSystems) {
        Map<String, Map<String, Coding>> codingMap = new HashMap<>();
        Map<String, Coding> codings;

        for (Map.Entry<String, FHIRTerminologyDef> valueSetEntry : valueSets.entrySet()) {
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
                    }
                    else {
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

    public static boolean isMultiDataType(ElementDefinition elementDefinition) {
        List<ElementDefinition.TypeRefComponent> typeList = elementDefinition.getType();
        return typeList.size() > 1;
    }

    public static String getTypeCodeOfElementDef(ElementDefinition elementDefinition) {
        String typeCode = null;
        List<ElementDefinition.TypeRefComponent> typeList = elementDefinition.getType();
        if (typeList.size() > 0) {
            typeCode = typeList.get(0).getCode();
        }
        return typeCode;
    }

    public static boolean canSkip(StructureDefinition profileDefinition, ElementDefinition element) {
        if(profileDefinition.getType().equals(element.getPath())){
            return true;
        }

        // Cardinality
        String max = element.getMax();
        if("0".equals(max)){
            return true;
        }

        String type = getTypeCodeOfElementDef(element);

        if("Extension".equals(type) && !element.hasSliceName()){
            return true;
        }

        String internalFPath = element.getPath().substring(profileDefinition.getType().length() + 1);
        return defaultSkippedProperties.contains(internalFPath);
    }
}
