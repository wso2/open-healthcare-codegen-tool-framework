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

import java.util.HashMap;
import java.util.Map;

public class HL7Spec {

    private String version;
    private final Map<String, HL7MessageDef> messageDefinitions;
    private final Map<String, HL7SegmentDef> segmentDefinitions;
    private final Map<String, HL7DataTypeDef> dataTypeDefinitions;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    //create constructor
    public HL7Spec(String version) {
        this.version = version;
        this.messageDefinitions = new HashMap<>();
        this.segmentDefinitions = new HashMap<>();
        this.dataTypeDefinitions = new HashMap<>();
    }

    public Map<String, HL7MessageDef> getMessageDefinitions() {
        return messageDefinitions;
    }

    public HL7MessageDef getMessageDefinition(String name) {
        return messageDefinitions.get(name);
    }

    public void addMessageDefinition(String name, HL7MessageDef messageDefinition) {
        messageDefinitions.put(name, messageDefinition);
    }

    public Map<String, HL7SegmentDef> getSegmentDefinitions() {
        return segmentDefinitions;
    }

    public HL7SegmentDef getSegmentDefinition(String name) {
        return segmentDefinitions.get(name);
    }

    public void addSegmentDefinition(String name, HL7SegmentDef segmentDefinition) {
        segmentDefinitions.put(name, segmentDefinition);
    }

    public Map<String, HL7DataTypeDef> getDataTypeDefinitions() {
        return dataTypeDefinitions;
    }

    public HL7DataTypeDef getDataTypeDefinition(String name) {
        return dataTypeDefinitions.get(name);
    }

    public void addDataTypeDefinition(String name, HL7DataTypeDef dataTypeDefinition) {
        dataTypeDefinitions.put(name, dataTypeDefinition);
    }
}
