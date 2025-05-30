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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.oas;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.hl7.fhir.r4.model.SearchParameter;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.OASGenUtils;

/**
 * Child Utility class for OAS generation for FHIR R4.
 */
public class R4OASGenUtils extends OASGenUtils {

    /**
     * Checks whether the parameter is already added to the operation.
     *
     * @param param     search parameter
     * @param operation OAS operation
     * @return true if parameter is added
     */
    protected static boolean isAdded(SearchParameter param, Operation operation) {
        if (operation.getParameters() != null) {
            for (Parameter parameter : operation.getParameters()) {
                if (parameter.getName().equals(param.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }
}
