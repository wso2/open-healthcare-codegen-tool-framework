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

import org.apache.velocity.VelocityContext;

import java.util.Map;

/**
 * Template context class to hold the context properties for the specific template type.
 */
public class TemplateContext {
    private VelocityContext velocityCtx;

    public TemplateContext() {
        velocityCtx = new VelocityContext();
    }

    public VelocityContext getVelocityCtx() {
        return velocityCtx;
    }

    public void setVelocityCtx(VelocityContext velocityCtx) {
        this.velocityCtx = velocityCtx;
    }

    /**
     * Sets given template context property.
     *
     * @param key   context property name
     * @param value context property value
     */
    public void setProperty(String key, Object value) {
        velocityCtx.put(key, value);
    }

    /**
     * Sets given context properties to the template context.
     *
     * @param values Map of context property values
     */
    public void setProperties(Map<String, Object> values) {
        for (String key : values.keySet()) {
            velocityCtx.put(key, values.get(key));
        }
    }
}
