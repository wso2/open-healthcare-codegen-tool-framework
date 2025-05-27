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

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.CardinalityTypes;

import java.util.List;

// S = StructureDefinition
public interface FHIRDataTypeDef <S, E> extends SpecModel {
    S getDefinition();

    void setDefinition(S definition);

    /**
     * Extracts max cardinality value for the datatype field for the given FHIR path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return max cardinality value which is a number or *
     */
    String getMaxCardinality(String fhirPath);

    /**
     * Extracts max cardinality and returns interpreted cardinality value for the datatype field for the given fhir path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return {@link CardinalityTypes} interpretation for the cardinality for the FHIR tool lib.
     */
    CardinalityTypes getMaxCardinalityType(String fhirPath);

    /**
     * Extracts min cardinality value for the datatype field for the given FHIR path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return min cardinality value which is a number
     */
    String getMinCardinality(String fhirPath);

    /**
     * Extracts min cardinality and returns interpreted cardinality value for the datatype field for the given fhir path
     *
     * @param fhirPath FHIR Path value representing the data type field(eg: Identifier.value)
     * @return {@link CardinalityTypes} interpretation for the cardinality for the FHIR tool lib.
     */
    CardinalityTypes getMinCardinalityType(String fhirPath);

    List<E> getExtensions();

    /**
     * Returns data type name.
     *
     * @return Data type name
     */
    public abstract String getTypeName();
}
