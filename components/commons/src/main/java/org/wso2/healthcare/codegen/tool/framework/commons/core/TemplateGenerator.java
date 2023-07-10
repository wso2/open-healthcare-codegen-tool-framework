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

package org.wso2.healthcare.codegen.tool.framework.commons.core;

import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;

import java.util.Map;

/**
 * This interface is implemented to have a tool artifact generator for a specific tool for a protocol.
 */
public interface TemplateGenerator {

    /**
     * Holds the generation logic for the tool output artifacts.
     *
     * @param templateEngine      {@link TemplateEngine} template engine holds utilities to generate artifacts
     * @param toolContext         {@link ToolContext} holds contextual data for the tool
     * @param generatorProperties holds additional arguments needed
     * @throws CodeGenException
     */
    void generate(TemplateEngine templateEngine, ToolContext toolContext, Map<String, Object> generatorProperties)
            throws CodeGenException;

    /**
     * Holds the generation logic for the tool output artifacts.
     *
     * @param toolContext         {@link ToolContext} holds contextual data for the too
     * @param generatorProperties holds additional arguments needed
     * @throws CodeGenException
     */
    void generate(ToolContext toolContext, Map<String, Object> generatorProperties) throws CodeGenException;

    /**
     * Sets additional arguments needed for the tool.
     *
     * @param property argument name
     * @param value    argument value
     */
    void setTemplateEngineProperty(String property, String value);

    /**
     * This is used to set the template resources path to apply from the template engine
     *
     * @param path template resource files path
     */
    void setTemplateResourcePath(String path) throws CodeGenException;

    /**
     * Returns child template generator if registered
     *
     * @return {@link TemplateGenerator} instance
     */
    TemplateGenerator getChildTemplateGenerator();

    /**
     * Returns additional arguments for the tool.
     *
     * @return
     */
    Map<String, Object> getGeneratorProperties();

    /**
     * Sets additional arguments for the tool.
     *
     * @param generatorProperties
     */
    void setGeneratorProperties(Map<String, Object> generatorProperties);

    void setTargetDir(String targetDir);

    String getTargetDir();
}
