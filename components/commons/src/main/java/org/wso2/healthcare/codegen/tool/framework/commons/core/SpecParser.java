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

/**
 * Interface that needs to be implemented to parse the protocol specifications and interprete the spec data needed
 * for the protocol level tool library.
 */
public interface SpecParser {

    /**
     * Parses protocol specifications and interprete the spec data to be used by the tools.
     *
     * @param toolConfig {@link ToolConfig} protocol level tool configuration.
     */
    void parse(ToolConfig toolConfig);
}
