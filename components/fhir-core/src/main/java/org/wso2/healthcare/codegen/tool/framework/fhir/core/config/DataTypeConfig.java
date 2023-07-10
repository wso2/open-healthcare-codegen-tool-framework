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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.consensys.cava.toml.TomlTable;

import java.util.Properties;

public class DataTypeConfig {

    private String name;
    private JsonArray childFields;
    private Properties properties;

    public DataTypeConfig(JsonObject config) {
        this.setName(config.getAsJsonPrimitive("name").getAsString());
        this.setChildFields(config.getAsJsonArray("childProperties"));
    }

    public DataTypeConfig(TomlTable config) {
        this.setName(config.getString("name"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonArray getChildFields() {
        return childFields;
    }

    public void setChildFields(JsonArray childFields) {
        this.childFields = childFields;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
