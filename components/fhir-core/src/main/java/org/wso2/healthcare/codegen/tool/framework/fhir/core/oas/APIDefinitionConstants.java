/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.healthcare.codegen.tool.framework.fhir.core.oas;

public class APIDefinitionConstants {
    /**
     * Store the Constants needed to API definition Generator.
     */
    public static final String CONTENT_TYPE_FHIR_JSON = "application/fhir+json";
    public static final String CONTENT_TYPE_FHIR_XML = "application/fhir+xml";
    public static final String FHIR_VERSION_R4 = "4.0.1";
    public static final String FHIR_VERSION_R5 = "5.0.0";

    public static final String DATA_TYPE_BACKBONE = "BackboneElement";

    public static final String OAS_EXTENSION_OH_FHIR_RESOURCE_TYPE = "x-wso2-oh-fhir-resourceType";
    public static final String OAS_EXTENSION_OH_FHIR_PROFILE = "x-wso2-oh-fhir-profile";
    public static final String OAS_REF_SCHEMAS = "#/components/schemas/";
    public static final String OAS_REF_REQUEST_BODIES = "#/components/requestBodies/";
    public static final String OAS_REF_PARAMETERS = "#/components/parameters/";
}
