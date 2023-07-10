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

import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;

/**
 * This interface is used to implement a codegen tool for a healthcare specification(i.e FHIR, HL7).
 */
public interface Tool {

    /**
     * Contains initialization logic for a codegen tool implementation. The tool can be use the tool config to access
     * information regarding tool specific configuration and write logic.
     *
     * @param toolConfig {@link ToolConfig} tool specific configuration
     */
    void initialize(ToolConfig toolConfig) throws CodeGenException;

    /**
     * Contains the execution logic of the codegen tool. This can have the tool specific logic needed to be done
     * before generating the target artifacts from the tool.
     *
     * @param toolContext {@link ToolContext} tool context instance holding contextual data specific to the protocol
     */
    TemplateGenerator execute(ToolContext toolContext) throws CodeGenException;

    /**
     * Returns protocol specific(i.e: FHIR, HL7) tool context.
     *
     * @return {@link ToolContext} instance
     */
    ToolContext getToolContext() throws CodeGenException;

    /**
     * Sets the protocol specific tool context for the tool.
     *
     * @param toolContext {@link ToolContext} instance
     */
    void setToolContext(ToolContext toolContext) throws CodeGenException;
}
