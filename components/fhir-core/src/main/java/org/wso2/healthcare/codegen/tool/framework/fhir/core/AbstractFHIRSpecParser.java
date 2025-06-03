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

package org.wso2.healthcare.codegen.tool.framework.fhir.core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.logging.Log;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.core.AbstractSpecParser;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;

/**
 * This interface is responsible delegating the parsing of FHIR specification files between versions.
 */

public abstract class AbstractFHIRSpecParser extends AbstractSpecParser {

    /**
     * This method is used to parse the FHIR structure definition from the file.
     *
     * @param file definition file
     * @return parsed FHIR structure definition
     * @throws CodeGenException if an error occurs while parsing the definition
     */
    protected static IBaseResource parseDefinition(FhirContext CTX, File file) throws CodeGenException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new CodeGenException("Error occurred while parsing FHIR definition.", e);
        }
    }

    /**
     * This method is used to parse the FHIR structure definition from the input stream.
     *
     * @param inputStream definition file input stream
     * @return parsed FHIR structure definition
     * @throws CodeGenException if an error occurs while parsing the definition
     */
    protected static IBaseResource parseDefinition(FhirContext CTX, InputStream inputStream) throws CodeGenException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(inputStream);
        } catch (Exception e) {
            throw new CodeGenException("Error occurred while parsing FHIR definition from the stream.", e);
        }
    }

    /**
     * This method is used to parse the FHIR structure definition from the string.
     *
     * @param resourceContent FHIR resource string
     * @return parsed FHIR structure definition
     * @throws CodeGenException if an error occurs while parsing the definition
     */
    protected static IBaseResource parseDefinition(FhirContext CTX, String resourceContent) throws CodeGenException {
        IParser parser = CTX.newJsonParser();
        try {
            return parser.parseResource(resourceContent);
        } catch (Exception e) {
            throw new CodeGenException("Error occurred while parsing FHIR definition from the string.", e);
        }
    }

    public abstract void parse(ToolConfig toolConfig);

    public abstract void parseIG(ToolConfig toolConfig, String igName, String igDirPath);

    protected abstract void populateBaseDataTypes();

    protected abstract void populateValues();

    protected abstract void populateCommonSearchParameters() throws CodeGenException;

    /**
     * This method will check whether the given file is a valid FHIR definition file.
     *
     * @param definitionFile definition file
     * @return true if the file is a valid FHIR definition file, false otherwise
     */
    protected boolean isValidFHIRDefinition(File definitionFile, Log LOG) {
        try (FileReader fileReader = new FileReader(definitionFile)) {
            JsonElement jsonElement = new Gson().fromJson(fileReader, JsonElement.class);
            if (!jsonElement.isJsonArray()) {
                return jsonElement.getAsJsonObject().has("resourceType");
            }
        } catch (IOException e) {
            LOG.error("Error occurred while reading the definition file: " + definitionFile.getName(), e);
        }
        return false;
    }
}
