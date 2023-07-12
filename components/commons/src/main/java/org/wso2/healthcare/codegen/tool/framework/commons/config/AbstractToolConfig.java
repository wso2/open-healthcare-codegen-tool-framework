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

package org.wso2.healthcare.codegen.tool.framework.commons.config;

import org.wso2.healthcare.codegen.tool.framework.commons.model.ConfigType;
import java.util.Properties;

/**
 * Abstract class which implements ToolConfig interface.
 */
public abstract class AbstractToolConfig implements ToolConfig {
    private ConfigType<?> configObj;
    private Properties properties = new Properties();
    private String configHomeDir;
    private String targetDir;
    private String toolName;

    public ConfigType<?> getConfigObj() {
        return configObj;
    }

    public void setConfigObj(ConfigType<?> configObj) {
        this.configObj = configObj;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getConfigHomeDir() {
        return configHomeDir;
    }

    public void setConfigHomeDir(String configHomeDir) {
        this.configHomeDir = configHomeDir;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
}
