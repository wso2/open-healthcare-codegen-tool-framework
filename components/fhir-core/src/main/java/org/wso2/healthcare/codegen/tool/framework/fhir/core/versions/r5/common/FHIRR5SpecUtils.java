package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common;

import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRTerminologyDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return null;
    }

    public static boolean isMultiDataType(ElementDefinition elementDefinition) {
        return false;
    }

    public static String getTypeCodeOfElementDef(ElementDefinition elementDefinition) {
        return null;
    }

    public static boolean canSkip(StructureDefinition structureDefinition, ElementDefinition elementDefinition) {
        return false;
    }
}
