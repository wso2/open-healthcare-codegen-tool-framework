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

package org.wso2.healthcare.codegen.tool.framework.hl7.core.config;

import java.util.List;

public class HL7SpecSchemaConfig {

    private String name;
    private String version;
    private String messageSchemaDirPath;
    private String dataTypesXSDFilePath;
    private String fieldsXSDFilePath;
    private String segmentsXSDFilePath;
    private boolean isEnable;
    private List<String> includedMessageSchemas;
    private List<String> excludedMessageSchemas;

    public HL7SpecSchemaConfig(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMessageSchemaDirPath() {
        return messageSchemaDirPath;
    }

    public void setMessageSchemaDirPath(String messageSchemaDirPath) {
        this.messageSchemaDirPath = messageSchemaDirPath;
    }

    public String getDataTypesXSDFilePath() {
        return dataTypesXSDFilePath;
    }

    public void setDataTypesXSDFilePath(String dataTypesXSDFilePath) {
        this.dataTypesXSDFilePath = dataTypesXSDFilePath;
    }

    public String getFieldsXSDFilePath() {
        return fieldsXSDFilePath;
    }

    public void setFieldsXSDFilePath(String fieldsXSDFilePath) {
        this.fieldsXSDFilePath = fieldsXSDFilePath;
    }

    public String getSegmentsXSDFilePath() {
        return segmentsXSDFilePath;
    }

    public void setSegmentsXSDFilePath(String segmentsXSDFilePath) {
        this.segmentsXSDFilePath = segmentsXSDFilePath;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public List<String> getIncludedMessageSchemas() {
        return includedMessageSchemas;
    }

    public void setIncludedMessageSchemas(List<String> includedMessageSchemas) {
        this.includedMessageSchemas = includedMessageSchemas;
    }

    public List<String> getExcludedMessageSchemas() {
        return excludedMessageSchemas;
    }

    public void setExcludedMessageSchemas(List<String> excludedMessageSchemas) {
        this.excludedMessageSchemas = excludedMessageSchemas;
    }

    public void addIncludedMessageSchema(String includedMessageSchema) {
        this.includedMessageSchemas.add(includedMessageSchema);
    }

    public void addExcludedMessageSchema(String excludedMessageSchema) {
        this.excludedMessageSchemas.add(excludedMessageSchema);
    }
}
