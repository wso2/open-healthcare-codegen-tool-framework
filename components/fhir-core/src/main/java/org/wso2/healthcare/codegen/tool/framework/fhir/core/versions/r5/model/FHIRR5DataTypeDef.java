package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.model.FHIRDataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.CardinalityTypes;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

import java.util.List;

public class FHIRR5DataTypeDef implements FHIRDataTypeDef<StructureDefinition, Extension> {

    private StructureDefinition definition;
    private DefKind kind;

    /**
     * Returns parsed structure definition model for the data type.
     *
     * @return {@link org.hl7.fhir.r5.model.StructureDefinition} instance for the data type definition
     */
    @Override
    public StructureDefinition getDefinition() {
        return this.definition;
    }

    @Override
    public void setDefinition(StructureDefinition definition) {
        this.definition = definition;
    }

    @Override
    public String getMaxCardinality(String fhirPath) {
        List<ElementDefinition> elementDefinitions = definition.getSnapshot().getElement();
        for(ElementDefinition elementDefinition : elementDefinitions){
            if(fhirPath.equals(elementDefinition.getPath())){
                return elementDefinition.getMax();
            }
        }
        return null;
    }

    /**
     * Extracts max cardinality value for the datatype field for the given FHIR path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return max cardinality value which is a number or *
     */
    @Override
    public CardinalityTypes getMaxCardinalityType(String fhirPath) {
        String maxCardinality = this.getMaxCardinality(fhirPath);
        if(StringUtils.isNotBlank(maxCardinality)){
            return CardinalityTypes.fromValue("max", maxCardinality);
        }
        return CardinalityTypes.INVALID;
    }

    /**
     * Extracts min cardinality value for the datatype field for the given FHIR path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return min cardinality value which is a number
     */
    @Override
    public String getMinCardinality(String fhirPath) {
        List<ElementDefinition> elementDefinitions = definition.getSnapshot().getElement();
        for(ElementDefinition elementDefinition : elementDefinitions){
            if(fhirPath.equals(elementDefinition.getPath())){
                return String.valueOf(elementDefinition.getMin());
            }
        }
        return null;
    }

    /**
     * Extracts min cardinality and returns interpreted cardinality value for the datatype field for the given fhir path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return {@link CardinalityTypes} interpretation for the cardinality for the FHIR tool lib.
     */
    @Override
    public CardinalityTypes getMinCardinalityType(String fhirPath) {
        String minCardinality = this.getMinCardinality(fhirPath);
        if (StringUtils.isNotBlank(minCardinality)) {
            return CardinalityTypes.fromValue("min", minCardinality);
        }
        return CardinalityTypes.INVALID;
    }

    @Override
    public List<Extension> getExtensions() {
        return definition.getExtension();
    }

    /**
     * Returns data type kind. values("primary-type", "complex-type", "invalid").
     *
     * @return Data type kind
     */
    public DefKind getKind() {
        return kind;
    }

    public void setKind(DefKind kind) {
        this.kind = kind;
    }

    /**
     * Returns data type name.
     *
     * @return Data type name
     */
    @Override
    public String getTypeName() {
        return definition.getName();
    }
}
