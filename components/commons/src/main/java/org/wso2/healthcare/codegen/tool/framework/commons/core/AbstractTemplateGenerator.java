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

import java.util.HashMap;
import java.util.Map;

/**
 * Holds abstract level for the template generator implementation.
 */
public abstract class AbstractTemplateGenerator implements TemplateGenerator {
    private TemplateEngine templateEngine;
    private TemplateGenerator childTemplateGenerator;
    private Map<String, Object> generatorProperties;
    //target directory for the generated artifacts for the generator
    private String targetDir;

    public AbstractTemplateGenerator(String templateResourcePath, String targetDir) throws CodeGenException {
        this.templateEngine = new TemplateEngine();
        this.setTemplateResourcePath(templateResourcePath);
        this.setTargetDir(targetDir);
        this.generatorProperties = new HashMap<>();
    }

    public AbstractTemplateGenerator(String templateResourcePath, String targetDir,
                                     Map<String, Object> generatorProperties) throws CodeGenException {
        this.templateEngine = new TemplateEngine();
        this.setTemplateResourcePath(templateResourcePath);
        this.setTargetDir(targetDir);
        this.generatorProperties = generatorProperties;
    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public TemplateContext getNewTemplateContext() {
        return new TemplateContext();
    }

    public void setTemplateEngineProperty(String property, String value) {
        templateEngine.setProperty(property, value);
    }

    public void setTemplateResourcePath(String path) throws CodeGenException {
        templateEngine.setTemplateResourcePath(path);
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public TemplateGenerator getChildTemplateGenerator() {
        return childTemplateGenerator;
    }

    public void setChildTemplateGenerator(TemplateGenerator childTemplateGenerator) {
        this.childTemplateGenerator = childTemplateGenerator;
    }

    public Map<String, Object> getGeneratorProperties() {
        return generatorProperties;
    }

    public void setGeneratorProperties(Map<String, Object> generatorProperties) {
        this.generatorProperties = generatorProperties;
    }
}
