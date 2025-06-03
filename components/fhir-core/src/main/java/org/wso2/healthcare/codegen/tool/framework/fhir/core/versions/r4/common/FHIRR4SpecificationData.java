/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org).
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

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ElementDefinition;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.model.FHIRR4DataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

import java.util.HashMap;
import java.util.Map;

/**
 * Data holder for parsed FHIR specification data and utilities.
 */
public class FHIRR4SpecificationData extends FHIRSpecificationData {

    private static final FHIRR4SpecificationData DATA_HOLDER_INSTANCE = new FHIRR4SpecificationData();
    private Map<String, Map<String, Coding>> resolvedTerminologies;

    private FHIRR4SpecificationData() {
        super();
        this.resolvedTerminologies = new HashMap<>();
    }

    public static FHIRR4SpecificationData getDataHolderInstance() {
        return DATA_HOLDER_INSTANCE;
    }

    public void setTerminologies() {
        this.resolvedTerminologies = FHIRR4SpecUtils.resolveTerminology(this.valueSets, this.codeSystems);
    }

    public Map<String, Map<String, Coding>> getTerminologies() {
        return resolvedTerminologies;
    }

    public boolean isPrimitiveDataType(ElementDefinition elementDefinition) {
        if (!FHIRR4SpecUtils.isMultiDataType(elementDefinition)) {
            String typeCode = FHIRR4SpecUtils.getTypeCodeOfElementDef(elementDefinition);
            FHIRR4DataTypeDef dataTypeDef = (FHIRR4DataTypeDef) dataTypes.get(typeCode);
            return dataTypeDef != null && DefKind.PRIMARY_TYPE.equals(dataTypeDef.getKind());
        }
        return false;
    }

    public boolean isMultiDatatype(ElementDefinition elementDefinition) {
        return FHIRR4SpecUtils.isMultiDataType(elementDefinition);
    }
}
