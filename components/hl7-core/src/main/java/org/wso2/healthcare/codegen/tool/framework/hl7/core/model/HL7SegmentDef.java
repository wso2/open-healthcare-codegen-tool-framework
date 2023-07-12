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
 * Holds HL7 segment metadata information.
 */
public class HL7SegmentDef implements SpecModel {

    private String name;
    private boolean required;
    private int maxRepetitions;
    private int minCardinality;
    private String segmentGroupName;
    private List<HL7FieldDef> fieldDefs;

    public HL7SegmentDef() {
        this.fieldDefs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSegmentGroupName() {
        return segmentGroupName;
    }

    public void setSegmentGroupName(String segmentGroupName) {
        this.segmentGroupName = segmentGroupName;
    }

    public List<HL7FieldDef> getFieldDefs() {
        return fieldDefs;
    }

    public void setFieldDefs(List<HL7FieldDef> fieldDefs) {
        this.fieldDefs = fieldDefs;
    }

    public void addFieldDef(HL7FieldDef fieldDef) {
        this.fieldDefs.add(fieldDef);
    }

}
