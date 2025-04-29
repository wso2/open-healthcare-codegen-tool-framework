package org.wso2.healthcare.codegen.tool.framework.fhir.core.oas;

import io.swagger.v3.oas.models.OpenAPI;

public interface OASGenerator {
    static OASGenerator getInstance() {
        return null;
    }

    OpenAPI getFhirOASBaseStructure();

    private void populateFhirOASBaseStructure() {

    }


}
