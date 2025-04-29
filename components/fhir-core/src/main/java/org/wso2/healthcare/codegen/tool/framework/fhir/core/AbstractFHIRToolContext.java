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

package org.wso2.healthcare.codegen.tool.framework.fhir.core;

import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.core.AbstractToolContext;
import org.wso2.healthcare.codegen.tool.framework.commons.core.SpecificationData;

/**
 * Generic Context for FHIR tools across different versions (R4, R5)
 */

public abstract class AbstractFHIRToolContext extends AbstractToolContext {

    @Override
    public ToolConfig getConfig() {
        return null;
    }

    protected void setConfig(ToolConfig toolConfig) {

    }

    @Override
    public SpecificationData getSpecificationData() {
        return null;
    }

    protected void setSpecificationData(SpecificationData specificationData) {

    }
}