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

package org.wso2.healthcare.codegen.tool.framework.hl7.core;

import org.wso2.healthcare.codegen.tool.framework.commons.core.AbstractTemplateGenerator;
import org.wso2.healthcare.codegen.tool.framework.commons.core.TemplateEngine;
import org.wso2.healthcare.codegen.tool.framework.commons.core.ToolContext;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;

import java.util.Map;

/**
 * Abstract implementation of the HL7 template generator that needs to be implemented by HL7 tool developers.
 */
public class AbstractHL7TemplateGenerator extends AbstractTemplateGenerator {

    public AbstractHL7TemplateGenerator(String targetDir) throws CodeGenException {
        super(targetDir);
    }
    @Override
    public void generate(TemplateEngine templateEngine, ToolContext toolContext,
                         Map<String, Object> generatorProperties) throws CodeGenException {
        throw new CodeGenException("Concrete implementation for template generator cannot be found for: " +
                toolContext.getConfig().getToolName());
    }

    @Override
    public void generate(ToolContext toolContext, Map<String, Object> generatorProperties) throws CodeGenException {
        throw new CodeGenException("Concrete implementation for template generator cannot be found for: " +
                toolContext.getConfig().getToolName());
    }
}
