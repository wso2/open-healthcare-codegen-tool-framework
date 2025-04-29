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
