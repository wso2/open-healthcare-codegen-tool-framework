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

import java.util.Map;

/**
 * Holds HL7 group metadata information.
 */
public class HL7GroupDef implements SpecModel {

    private String name;
    private boolean required;
    private int maxRepetitions;
    private int minCardinality;
    Map<String, HL7SegmentDef> fields;
    Map<String, HL7GroupDef> subGroups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, HL7SegmentDef> getFields() {
        return fields;
    }

    public void setFields(Map<String, HL7SegmentDef> fields) {
        this.fields = fields;
    }

    public void addField(HL7SegmentDef field) {
        fields.put(field.getName(), field);
    }

    public HL7SegmentDef getField(String name) {
        return fields.get(name);
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getMaxRepetitions() {
        return maxRepetitions;
    }

    public void setMaxRepetitions(int maxRepetitions) {
        this.maxRepetitions = maxRepetitions;
    }

    public int getMinCardinality() {
        return minCardinality;
    }

    public void setMinCardinality(int minCardinality) {
        this.minCardinality = minCardinality;
    }

    public Map<String, HL7GroupDef> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(Map<String, HL7GroupDef> subGroups) {
        this.subGroups = subGroups;
    }

}
