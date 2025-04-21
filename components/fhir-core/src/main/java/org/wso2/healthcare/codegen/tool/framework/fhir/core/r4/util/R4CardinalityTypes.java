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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.util;

import java.util.regex.Pattern;

public enum R4CardinalityTypes {
    MAX_UNLIMITED("unlimited"),
    MAX_FINITE("finite"),
    REQUIRED("required"),
    OPTIONAL("optional"),
    INVALID("invalid");

    private final String cardinalityStr;

    R4CardinalityTypes(String cardinalityStr) {
        this.cardinalityStr = cardinalityStr;
    }

    public static R4CardinalityTypes fromValue(String type, String code) {
        if (code.equals("*")) {
            return R4CardinalityTypes.MAX_UNLIMITED;
        } else if (code.equals("0")) {
            return R4CardinalityTypes.OPTIONAL;
        } else if (Pattern.matches("([1-9][0-9]*)", code)) {
            switch (type) {
                case "min":
                    return R4CardinalityTypes.REQUIRED;
                case "max":
                    return R4CardinalityTypes.MAX_FINITE;
                default:
                    return R4CardinalityTypes.INVALID;
            }
        }
        return R4CardinalityTypes.INVALID;
    }
}
