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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.common;

import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Coding;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.common.FHIRSpecificationData;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.model.FHIRR5DataTypeDef;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.util.DefKind;

import java.util.Map;
import java.util.HashMap;

public class FHIRR5SpecificationData extends FHIRSpecificationData {

    private static final FHIRR5SpecificationData DATA_HOLDER_INSTANCE = new FHIRR5SpecificationData();

    private Map<String, Map<String, Coding>> resolvedTerminologies;

    private FHIRR5SpecificationData() {
        super();
        this.resolvedTerminologies = new HashMap<>();
    }

    public static FHIRR5SpecificationData getDataHolderInstance() {
        return DATA_HOLDER_INSTANCE;
    }

    public void setTerminologies() {
        this.resolvedTerminologies = FHIRR5SpecUtils.resolveTerminology(this.valueSets, this.codeSystems);
    }

    public Map<String, Map<String, Coding>> getTerminologies() {
        return resolvedTerminologies;
    }

    public boolean isPrimitiveDataType(ElementDefinition elementDefinition) {
        if (!FHIRR5SpecUtils.isMultiDataType(elementDefinition)) {
            String typeCode = FHIRR5SpecUtils.getTypeCodeOfElementDef(elementDefinition);
            FHIRR5DataTypeDef dataTypeDef = (FHIRR5DataTypeDef) dataTypes.get(typeCode);
            return dataTypeDef != null && DefKind.PRIMARY_TYPE.equals(dataTypeDef.getKind());
        }
        return false;
    }

    public boolean isMultiDatatype(ElementDefinition elementDefinition) {
        return FHIRR5SpecUtils.isMultiDataType(elementDefinition);
    }
}
