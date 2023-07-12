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

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Template engine implementation to be used to generate codegen tool(s) artifacts. Currently this is a wrapper
 * implementation for the velocity template engine.
 */
public class TemplateEngine {

    private final VelocityEngine velocityEngine;

    public TemplateEngine() {
        this.velocityEngine = new VelocityEngine();
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setProperty(String property, String value) {
        this.velocityEngine.setProperty(property, value);
    }

    protected void init() throws CodeGenException {
        try {
            velocityEngine.init();
        } catch (Exception e) {
            throw new CodeGenException("Error occurred while initializing template engine.", e);
        }
    }

    /**
     * This is used to load the template resources from the provided path
     * @param path resource path.
     */
    public void setTemplateResourcePath(String path) throws CodeGenException {
        this.velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, path);
        this.init();
    }

    /**
     * Used to generate file based on template.
     *
     * @param templateName name of the velocity template.
     * @param templateContext velocity context with included info
     * @param fileName filename
     * @throws CodeGenException read/write error
     */
    public void generateOutputAsFile(String templateName, TemplateContext templateContext,
                                     String directoryPath, String fileName) throws CodeGenException {

        createNestedDirectory(directoryPath);
        try (Writer writer = new FileWriter(directoryPath + fileName)) {
            this.velocityEngine.mergeTemplate(templateName, Charset.defaultCharset().toString(),
                    templateContext.getVelocityCtx(), writer);
        } catch (IOException e) {
            throw new CodeGenException("Error occurred while accessing output file", e);
        } catch (Exception e) {
            throw new CodeGenException("Error occurred while generating output file.", e);
        }
    }

    /**
     * Creates nested directory structure.
     *
     * @param nestedPath path to check if directory exist.
     * @throws CodeGenException error in file system access
     */
    public void createNestedDirectory(String nestedPath) throws CodeGenException {

        Path pathFoSubFolder =  Paths.get(nestedPath);
        try {
            if (!Files.exists(pathFoSubFolder)) {
                Files.createDirectories(pathFoSubFolder);
            }
        } catch (IOException e) {
            throw new CodeGenException("Error occurred while creating nested directory on path: " + nestedPath, e);
        }
    }
}
