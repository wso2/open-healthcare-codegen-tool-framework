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
 * Holds all the contexual data needed for the protocol level(FHIR,HL7) tool library. Tool context will be shared
 * within the tool implementations for the same protocol.
 */
public interface ToolContext {

    /**
     * Returns tool specific configuration from the tool context.
     *
     * @return {@link ToolConfig} instance
     */
    ToolConfig getConfig();

    /**
     * Returns specification data for the specific protocol(i.e: FHIR, HL7) lib.
     *
     * @return
     */
    SpecificationData getSpecificationData();
}
