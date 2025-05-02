package org.wso2.healthcare.codegen.tool.framework.fhir.core;

import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r4.FHIRR4SpecParser;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.FHIRR5SpecParser;

public class FHIRSpecParserFactory {
    public static AbstractFHIRSpecParser getParser(String fhirVersion) {
        if (fhirVersion.equalsIgnoreCase("r4")) {
            return new FHIRR4SpecParser();
        }
        else if (fhirVersion.equalsIgnoreCase("r5")) {
            return new FHIRR5SpecParser();
        }
        else {
            throw new IllegalArgumentException("Unsupported FHIR Version. Supported versions are: r4, r5");
        }
    }
}
