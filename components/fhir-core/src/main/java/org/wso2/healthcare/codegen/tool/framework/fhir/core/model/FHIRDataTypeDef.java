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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.model;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.CardinalityTypes;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

import java.util.List;

public class FHIRDataTypeDef implements SpecModel {
    //TODO: filter the data type elements with element.id, element.extension filtered out
    private StructureDefinition definition;
    private DefKind kind;

    /**
     * Returns parsed structure definition model for the data type.
     *
     * @return {@link StructureDefinition} instance for the data type definition
     */
    public StructureDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(StructureDefinition definition) {
        this.definition = definition;
    }

    /**
     * Extracts max cardinality value for the datatype field for the given FHIR path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return max cardinality value which is a number or *
     */
    public String getMaxCardinality(String fhirPath) {
        List<ElementDefinition> elementDefinitions = definition.getSnapshot().getElement();
        for (ElementDefinition elementDefinition : elementDefinitions) {
            if (fhirPath.equals(elementDefinition.getPath())) {
                return elementDefinition.getMax();
            }
        }
        return null;
    }

    /**
     * Extracts max cardinality and returns interpreted cardinality value for the datatype field for the given fhir path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return {@link CardinalityTypes} interpretation for the cardinality for the FHIR tool lib.
     */
    public CardinalityTypes getMaxCardinalityType(String fhirPath) {
        String maxCardinality = this.getMaxCardinality(fhirPath);
        if (StringUtils.isNotBlank(maxCardinality)) {
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
    public String getMinCardinality(String fhirPath) {
        List<ElementDefinition> elementDefinitions = definition.getSnapshot().getElement();
        for (ElementDefinition elementDefinition : elementDefinitions) {
            if (fhirPath.equals(elementDefinition.getPath())) {
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
    public CardinalityTypes getMinCardinalityType(String fhirPath) {
        String minCardinality = this.getMinCardinality(fhirPath);
        if (StringUtils.isNotBlank(minCardinality)) {
            return CardinalityTypes.fromValue("min", minCardinality);
        }
        return CardinalityTypes.INVALID;
    }

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
    public String getTypeName() {
        return definition.getName();
    }
}
