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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.wso2.healthcare.codegen.tool.framework.commons.Constants;
import org.wso2.healthcare.codegen.tool.framework.commons.config.AbstractToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.commons.model.ConfigType;

import java.util.HashMap;
import java.util.Map;

public class HL7ToolConfig extends AbstractToolConfig {

    private final Map<String, HL7SpecSchemaConfig> schemaConfigMap = new HashMap<>();

    @Override
    public void configure(ConfigType<?> configObj) throws CodeGenException {
        this.setConfigObj(configObj);
        String type = configObj.getType();
        if (Constants.JSON_CONFIG_TYPE.equals(type)) {
            JsonObject jsonConfigObj = (JsonObject) configObj.getConfigObj();
            JsonArray hl7SpecSchemaConfig = jsonConfigObj.getAsJsonArray("HL7SpecSchema");
            if (hl7SpecSchemaConfig != null) {
                for (int i = 0; i < hl7SpecSchemaConfig.size(); i++) {
                    JsonObject hl7SpecSchema = hl7SpecSchemaConfig.get(i).getAsJsonObject();
                    String hl7SpecSchemaName = hl7SpecSchema.get("name").getAsString();
                    String hl7SpecSchemaVersion = hl7SpecSchema.get("version").getAsString();
                    if (StringUtils.isNotBlank(hl7SpecSchemaName)) {
                        HL7SpecSchemaConfig hl7SpecSchemaConfigObj = new HL7SpecSchemaConfig(hl7SpecSchemaName,
                                hl7SpecSchemaVersion);
                        hl7SpecSchemaConfigObj.setEnable(hl7SpecSchema.get("enable").getAsBoolean());
                        hl7SpecSchemaConfigObj.setMessageSchemaDirPath(hl7SpecSchema.get("messageXSDDir").getAsString());
                        hl7SpecSchemaConfigObj.setSegmentsXSDFilePath(hl7SpecSchema.get("segmentsXSDFile").getAsString());
                        hl7SpecSchemaConfigObj.setDataTypesXSDFilePath(hl7SpecSchema.get("dataTypesXSDFile").getAsString());
                        hl7SpecSchemaConfigObj.setFieldsXSDFilePath(hl7SpecSchema.get("fieldsXSDFile").getAsString());
                        schemaConfigMap.put(hl7SpecSchemaName, hl7SpecSchemaConfigObj);
                    }
                }
            }
        }
    }

    public Map<String, HL7SpecSchemaConfig> getSchemaConfigMap() {
        return schemaConfigMap;
    }
}
