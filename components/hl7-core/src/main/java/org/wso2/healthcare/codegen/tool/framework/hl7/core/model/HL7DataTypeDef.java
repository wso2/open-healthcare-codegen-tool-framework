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

package org.wso2.healthcare.codegen.tool.framework.hl7.core.model;

import org.wso2.healthcare.codegen.tool.framework.commons.model.SpecModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds HL7 data type meta data information.
 */
public class HL7DataTypeDef implements SpecModel {

    private String name;
    private String description;
    private boolean isComplexType;
    private List<HL7DataTypeDef> childTypes = new ArrayList<>();

    /**
     * Returns the datatype name without the dot which will be used to generate fields in the generated code.
     *
     * @return field name without the dot.
     */
    public String getFriendlyName() {
        return name.replaceAll("\\.", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComplexType() {
        return isComplexType;
    }

    public void setComplexType(boolean isComplexType) {
        this.isComplexType = isComplexType;
    }

    public List<HL7DataTypeDef> getChildTypes() {
        return childTypes;
    }

    public void setChildTypes(List<HL7DataTypeDef> childTypes) {
        this.childTypes = childTypes;
    }
}
