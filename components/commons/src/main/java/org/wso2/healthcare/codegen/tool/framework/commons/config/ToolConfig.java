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

import com.google.gson.JsonElement;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.commons.model.ConfigType;

/**
 * Interface for implementing new tool configurations for tool framework.
 */
public interface ToolConfig {

    /**
     * This method is to define the config fields from the config file representation.
     *
     * @param configObj config object
     * @throws CodeGenException if an error occurs while configuring the config object
     */
    void configure(ConfigType<?> configObj) throws CodeGenException;

    /**
     * Sets path to the config file directory.
     *
     * @param resourceHomeDir config file directory
     */
    void setResourceHomeDir(String resourceHomeDir);

    /**
     * Sets name of the tool.
     *
     * @param toolName name of the tool
     */
    void setToolName(String toolName);

    /**
     * Returns name of the tool.
     *
     * @return
     */
    String getToolName();

    /**
     * Sets directory path to the build artifacts.
     *
     * @param targetDir path to the target directory
     */
    void setTargetDir(String targetDir);

    String getSpecBasePath();

    void setSpecBasePath(String specBasePath);

    void overrideConfig(String jsonPath, JsonElement value);
}
