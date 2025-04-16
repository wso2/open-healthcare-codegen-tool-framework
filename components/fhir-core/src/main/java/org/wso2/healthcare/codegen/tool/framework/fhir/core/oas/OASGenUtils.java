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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.oas;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.Set;

/**
 * Parent Utility class for OAS generation.
 */
public class OASGenUtils {
    /**
     * Maps FHIR data type to OAS data type.
     *
     * @param fhirDataType FHIR data type
     * @return Mapped FHIR data type
     */
    public static String mapToOASDataType(String fhirDataType) {
        switch (fhirDataType) {
            case "object":
                return "object";
            case "int":
                return "int";
            default:
                return "string";
        }
    }

    /**
     * Returns relevant regex validations for FHIR data type.
     *
     * @param dataType FHIR data type
     * @return Regex expression
     */
    public static String getRegexForDataType(String dataType) {

        switch (dataType) {
            case "datetime":
                return "([0-9]([0-9]([0-9][1-9]|[1-9]0)|[1-9]00)|[1-9]000)(-(0[1-9]|1[0-2])(-(0[1-9]|" +
                        "[1-2][0-9]|3[0-1])" +
                        "(T([01][0-9]|2[0-3]):[0-5][0-9]:([0-5][0-9]|60)(\\.[0-9]+)?(Z|(\\+|-)((0[0-9]|1[0-3]):" +
                        "[0-5][0-9]|14:00)))?)?)?";
            case "string":
                return "[ \\r\\n\\t\\S]+";
            default:
                return "\\S*";
        }
    }

    /**
     * Generates description for FHIR API Definition.
     *
     * @param resourceType      FHIR resource type
     * @param supportedProfiles supported profiles
     * @return Generated description
     */
    public static String generateDescription(String resourceType, Set<String> supportedProfiles) {

        String mainText = "A simplified version of the HL7 FHIR API for " + resourceType + " resource.\n";
        StringBuilder stringBuilder = new StringBuilder(mainText);
        if (!supportedProfiles.isEmpty()) {
            stringBuilder.append("Supported Profiles: \n");
            for (String profile : supportedProfiles) {
                stringBuilder.append(profile).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Generate OpenAPI parameter object.
     *
     * @param paramName   parameter name
     * @param description description
     * @param schemaType  type
     * @param in          parameter category
     * @param isRequired  is required
     * @return OAS parameter
     */
    public static Parameter generateParameter(String paramName, String description, String schemaType,
                                                 String in, boolean isRequired) {
        Parameter parameter = new Parameter();
        parameter.setName(paramName);
        parameter.setIn(in);
        parameter.setRequired(isRequired);
        parameter.setDescription(description);
        Schema paramSchema = new Schema();
        paramSchema.setType("string");
        parameter.setSchema(paramSchema);
        //extension to keep FHIR Search parameter type: x-wso2-oh-fhirType
        //possible values: number | date | string | token | reference | composite | quantity | uri | special
        //ref: https://www.hl7.org/fhir/valueset-search-param-type.html
        if (!isRequired) {
            //extension will be added only to search parameters
            parameter.addExtension("x-wso2-oh-fhirType", schemaType);
        }
        return parameter;
    }

    /**
     * Returns OAS definition as a json
     *
     * @param openAPI OAS definition object
     * @return converted json
     */
    public String getOASJson(OpenAPI openAPI) {
        return Json.pretty(openAPI);
    }

    /**
     * Returns OAS definition as a yaml
     *
     * @param openAPI OAS definition object
     * @return converted yaml
     */
    public String getOASYaml(OpenAPI openAPI) {
        return Yaml.pretty(openAPI);
    }
}
