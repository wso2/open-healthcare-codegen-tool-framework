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

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.wso2.healthcare.codegen.tool.framework.commons.config.ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.core.AbstractSpecParser;
import org.wso2.healthcare.codegen.tool.framework.hl7.core.common.HL7SpecificationData;
import org.wso2.healthcare.codegen.tool.framework.hl7.core.config.HL7SpecSchemaConfig;
import org.wso2.healthcare.codegen.tool.framework.hl7.core.config.HL7ToolConfig;
import org.wso2.healthcare.codegen.tool.framework.hl7.core.model.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HL7SpecParser extends AbstractSpecParser {

    @Override
    public void parse(ToolConfig toolConfig) {
        Map<String, HL7SpecSchemaConfig> schemaConfigMap = ((HL7ToolConfig) toolConfig).getSchemaConfigMap();
        for (Map.Entry<String, HL7SpecSchemaConfig> entry : schemaConfigMap.entrySet()) {
            HL7SpecSchemaConfig schemaConfig = entry.getValue();
            if (schemaConfig == null || !schemaConfig.isEnable()) {
                continue;
            }
            HL7Spec hl7Spec = HL7SpecificationData.getDataHolderInstance().getHL7Spec(schemaConfig.getVersion());
            if (hl7Spec == null) {
                hl7Spec = new HL7Spec(schemaConfig.getVersion());
                HL7SpecificationData.getDataHolderInstance().addHL7Spec(schemaConfig.getVersion(), hl7Spec);
            }

            Map<String, HL7FieldDef> hl7FieldDefMap = parseAndReturnFieldsSchema(schemaConfig.getFieldsXSDFilePath());
            parseAndLoadMessageSchema(schemaConfig.getMessageSchemaDirPath(), hl7Spec);
            parseAndLoadSegmentSchema(schemaConfig.getSegmentsXSDFilePath(), hl7Spec, hl7FieldDefMap);
            parseAndLoadDataTypeSchema(schemaConfig.getDataTypesXSDFilePath(), hl7Spec);
        }
    }

    private void parseAndLoadMessageSchema(String messageSchemaDirPath, HL7Spec hl7Spec) {

        File msgSchemaDir = new File(messageSchemaDirPath);
        if (msgSchemaDir.isDirectory()) {
            File[] msgSchemaFiles = msgSchemaDir.listFiles();
            if (msgSchemaFiles != null) {
                for (File msgSchemaFile : msgSchemaFiles) {
                    if (msgSchemaFile.isFile() && msgSchemaFile.getName().endsWith(".xsd")) {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        try {
                            Document doc = dbFactory.newDocumentBuilder().parse(msgSchemaFile);
                            doc.getDocumentElement().normalize();

                            Map<String, Map<String, HL7SegmentDef>> complexTypeMap = new LinkedHashMap<>();
                            NodeList complexTypes = doc.getElementsByTagName("xsd:complexType");
                            if (complexTypes != null) {
                                for (int i = 0; i < complexTypes.getLength(); i++) {
                                    NamedNodeMap attributes = complexTypes.item(i).getAttributes();
                                    if (attributes.getNamedItem("name") != null) {
                                        String complexTypeName = attributes.getNamedItem("name").getNodeValue();
                                        Map<String, HL7SegmentDef> segments = complexTypeMap.get(complexTypeName);
                                        if (!complexTypeMap.containsKey(complexTypeName)) {
                                            segments = new LinkedHashMap<>();
                                            complexTypeMap.put(complexTypeName, segments);
                                        }
                                        NodeList childNodes = complexTypes.item(i).getChildNodes();
                                        for (int j = 0; j < childNodes.getLength(); j++) {
                                            if ("xsd:sequence".equals(childNodes.item(j).getNodeName())) {
                                                NodeList childElements = childNodes.item(j).getChildNodes();
                                                for (int k = 0; k < childElements.getLength(); k++) {
                                                    if ("xsd:element".equals(childElements.item(k).getNodeName())) {
                                                        NamedNodeMap childAttributes = childElements.item(k).getAttributes();
                                                        if (childAttributes != null &&
                                                                childAttributes.getNamedItem("ref") != null) {
                                                            String ref = childAttributes.getNamedItem("ref").getNodeValue();
                                                            HL7SegmentDef segment = new HL7SegmentDef();
                                                            segment.setName(ref);
                                                            String maxOccurs = childAttributes.getNamedItem(
                                                                    "maxOccurs").getNodeValue();
                                                            if ("unbounded".equals(maxOccurs)) {
                                                                segment.setMaxRepetitions(-1);
                                                            } else {
                                                                segment.setMaxRepetitions(Integer.parseInt(maxOccurs));
                                                            }
                                                            segment.setMinCardinality(Integer.parseInt(
                                                                    childAttributes.getNamedItem("minOccurs").getNodeValue()));
                                                            segments.put(ref, segment);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            NodeList elements = doc.getElementsByTagName("xsd:element");
                            if (elements != null) {
                                for (int i = 0; i < elements.getLength(); i++) {
                                    NamedNodeMap attributes = elements.item(i).getAttributes();
                                    if (attributes.getNamedItem("type") != null) {
                                        String elementName = attributes.getNamedItem("name").getNodeValue();
                                        if (!elementName.contains(".")) {
                                            HL7MessageDef messageDefinition = hl7Spec.getMessageDefinition(elementName);
                                            if (messageDefinition == null) {
                                                messageDefinition = new HL7MessageDef(elementName);
                                                hl7Spec.addMessageDefinition(elementName, messageDefinition);
                                            }
                                            Map<String, HL7SegmentDef> segmentDefMap =
                                                    complexTypeMap.get(attributes.getNamedItem("type").getNodeValue());
                                            if (segmentDefMap != null) {
                                                for (Map.Entry<String, HL7SegmentDef> segmentDefEntry : segmentDefMap.entrySet()) {
                                                    if (segmentDefEntry.getKey().startsWith(elementName + ".")) {
                                                        HL7GroupDef hl7GroupDef = new HL7GroupDef();
                                                        hl7GroupDef.setName(segmentDefEntry.getKey().substring(
                                                                elementName.length() + 1));
                                                        hl7GroupDef.setRequired(segmentDefEntry.getValue().isRequired());
                                                        hl7GroupDef.setMaxRepetitions(segmentDefEntry.getValue()
                                                                .getMaxRepetitions());
                                                        hl7GroupDef.setMinCardinality(segmentDefEntry.getValue()
                                                                .getMinCardinality());
                                                        String groupElement = segmentDefEntry.getKey().concat(".CONTENT");
                                                        Map<String, HL7SegmentDef> groupSegmentDefMap = complexTypeMap.get(groupElement);
                                                        if (groupSegmentDefMap != null) {
                                                            for (Map.Entry<String, HL7SegmentDef> groupSegmentDefEntry : groupSegmentDefMap.entrySet()) {
                                                                groupSegmentDefEntry.getValue().setSegmentGroupName(
                                                                        hl7GroupDef.getName());
                                                                messageDefinition.addSegment(
                                                                        groupSegmentDefEntry.getValue());
                                                            }
                                                        }
                                                        hl7GroupDef.setFields(complexTypeMap.get(groupElement));
                                                        messageDefinition.addGroup(hl7GroupDef);
                                                    } else {
                                                        messageDefinition.addSegment(segmentDefEntry.getValue());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (SAXException | IOException | ParserConfigurationException e) {
                            //handle exception
                        }
                    }
                }
            }
        }
    }

    private void parseAndLoadSegmentSchema(String segmentSchemaPath, HL7Spec hl7Spec, Map<String,
            HL7FieldDef> hl7FieldDefMap) {

        File segmentSchemaFile = new File(segmentSchemaPath);
        if (segmentSchemaFile.isFile() && segmentSchemaFile.getName().endsWith(".xsd")) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            try {
                Document doc = dbFactory.newDocumentBuilder().parse(segmentSchemaFile);
                doc.getDocumentElement().normalize();

                Map<String, HL7SegmentDef> segmentsDefMap = new LinkedHashMap<>();
                NodeList complexTypes = doc.getElementsByTagName("xsd:complexType");
                if (complexTypes != null) {
                    for (int i = 0; i < complexTypes.getLength(); i++) {
                        NamedNodeMap attributes = complexTypes.item(i).getAttributes();
                        if (attributes.getNamedItem("name") != null) {
                            String complexTypeName = attributes.getNamedItem("name").getNodeValue();
                            if (complexTypeName.endsWith(".CONTENT")) {
                                HL7SegmentDef hl7SegmentDef = new HL7SegmentDef();
                                hl7SegmentDef.setName(complexTypeName.substring(0, complexTypeName.length() - 8));

                                NodeList childNodes = complexTypes.item(i).getChildNodes();
                                for (int j = 0; j < childNodes.getLength(); j++) {
                                    if ("xsd:sequence".equals(childNodes.item(j).getNodeName())) {
                                        NodeList childElements = childNodes.item(j).getChildNodes();
                                        for (int k = 0; k < childElements.getLength(); k++) {
                                            if ("xsd:element".equals(childElements.item(k).getNodeName())) {
                                                NamedNodeMap childAttributes = childElements.item(k).getAttributes();
                                                if (childAttributes != null &&
                                                        childAttributes.getNamedItem("ref") != null) {
                                                    String ref = childAttributes.getNamedItem("ref").getNodeValue();
                                                    HL7FieldDef hl7FieldDef = new HL7FieldDef();
                                                    hl7FieldDef.setName(ref);
                                                    hl7FieldDef.setRequired(!"0".equals(childAttributes.getNamedItem(
                                                            "minOccurs").getNodeValue()));
                                                    hl7FieldDef.setMinCardinality(Integer.parseInt(
                                                            childAttributes.getNamedItem("minOccurs").getNodeValue()));
                                                    if (childAttributes.getNamedItem("maxOccurs") != null) {
                                                        String maxOccurs = childAttributes.getNamedItem("maxOccurs")
                                                                .getNodeValue();
                                                        if ("unbounded".equals(maxOccurs)) {
                                                            hl7FieldDef.setMaxRepetitions(-1);
                                                        } else {
                                                            hl7FieldDef.setMaxRepetitions(Integer.parseInt(maxOccurs));
                                                        }
                                                    } else {
                                                        hl7FieldDef.setMaxRepetitions(1);
                                                    }
                                                    if (hl7FieldDefMap != null && hl7FieldDefMap.get(ref) != null) {
                                                        hl7FieldDef.setBaseDataType(hl7FieldDefMap.get(ref)
                                                                .getBaseDataType());
                                                    }
                                                    hl7SegmentDef.addFieldDef(hl7FieldDef);
                                                }
                                            }
                                        }
                                    }
                                }
                                segmentsDefMap.putIfAbsent(complexTypeName, hl7SegmentDef);
                            }
                        }
                    }
                }

                NodeList elements = doc.getElementsByTagName("xsd:element");
                if (elements != null) {
                    for (int i = 0; i < elements.getLength(); i++) {
                        NamedNodeMap attributes = elements.item(i).getAttributes();
                        if (attributes.getNamedItem("type") != null) {
                            String segmentName = attributes.getNamedItem("name").getNodeValue();
                            hl7Spec.addSegmentDefinition(segmentName, segmentsDefMap.get(attributes.getNamedItem("type")
                                    .getNodeValue()));
                        }
                    }
                }
            } catch (SAXException | IOException | ParserConfigurationException e) {
                //handle exception
            }
        }
    }

    private void parseAndLoadDataTypeSchema(String dataTypeSchemaPath, HL7Spec hl7Spec) {

        File dataTypeSchemaFile = new File(dataTypeSchemaPath);
        if (dataTypeSchemaFile.isFile() && dataTypeSchemaFile.getName().endsWith(".xsd")) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            try {
                Document doc = dbFactory.newDocumentBuilder().parse(dataTypeSchemaFile);
                doc.getDocumentElement().normalize();

                Map<String, HL7DataTypeDef> dataTypesDefMap = new LinkedHashMap<>();
                NodeList simpleTypes = doc.getElementsByTagName("xsd:simpleType");
                if (simpleTypes != null) {
                    for (int i = 0; i < simpleTypes.getLength(); i++) {
                        NamedNodeMap attributes = simpleTypes.item(i).getAttributes();
                        if (attributes.getNamedItem("name") != null) {
                            String simpleTypeName = attributes.getNamedItem("name").getNodeValue();
                            HL7DataTypeDef hl7DataTypeDef = new HL7DataTypeDef();
                            hl7DataTypeDef.setName(simpleTypeName);
                            hl7DataTypeDef.setComplexType(false);
                            dataTypesDefMap.putIfAbsent(simpleTypeName, hl7DataTypeDef);
                        }
                    }
                }

                NodeList complexTypes = doc.getElementsByTagName("xsd:complexType");
                Map<String, HL7DataTypeDef> complexDataTypeChildElements = new LinkedHashMap<>();
                if (complexTypes != null) {

                    for (int i = 0; i < complexTypes.getLength(); i++) {
                        NamedNodeMap attributes = complexTypes.item(i).getAttributes();
                        HL7DataTypeDef hl7ComplexDataTypeDef = new HL7DataTypeDef();
                        if (attributes.getNamedItem("name") != null &&
                                !attributes.getNamedItem("name").getNodeValue().endsWith(".CONTENT")) {
                            hl7ComplexDataTypeDef.setName(attributes.getNamedItem("name").getNodeValue());
                            hl7ComplexDataTypeDef.setComplexType(true);
                            dataTypesDefMap.putIfAbsent(hl7ComplexDataTypeDef.getName(), hl7ComplexDataTypeDef);
                        }
                        NodeList childNodes = complexTypes.item(i).getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            if ("xsd:sequence".equals(childNodes.item(j).getNodeName())) {
                                NodeList childElements = childNodes.item(j).getChildNodes();
                                for (int k = 0; k < childElements.getLength(); k++) {
                                    if ("xsd:element".equals(childElements.item(k).getNodeName())) {
                                        NamedNodeMap childAttributes = childElements.item(k).getAttributes();
//                                        if (childAttributes != null &&
//                                                childAttributes.getNamedItem("ref") != null) {
//                                            String ref = childAttributes.getNamedItem("ref").getNodeValue();
//                                            HL7DataTypeDef hl7DataTypeDef = new HL7DataTypeDef();
//                                            hl7DataTypeDef.setName(ref);
//                                            if (hl7ComplexDataTypeDef.getName() != null) {
//                                                hl7ComplexDataTypeDef.getChildTypes().add(hl7DataTypeDef);

                                        if (childAttributes != null) {
                                            if (childAttributes.getNamedItem("ref") != null) {
                                                String name = childAttributes.getNamedItem("ref").getNodeValue();
                                                HL7DataTypeDef hl7DataTypeDef = new HL7DataTypeDef();
                                                hl7DataTypeDef.setName(name);
                                                if (hl7ComplexDataTypeDef.getName() != null) {
                                                    hl7ComplexDataTypeDef.getChildTypes().add(hl7DataTypeDef);
                                                }
                                                complexDataTypeChildElements.putIfAbsent(name, hl7DataTypeDef);
                                            } else if (childAttributes.getNamedItem("name") != null) {
                                                String name = childAttributes.getNamedItem("name").getNodeValue();
                                                HL7DataTypeDef hl7DataTypeDef = new HL7DataTypeDef();
                                                hl7DataTypeDef.setName(name);
                                                if (hl7ComplexDataTypeDef.getName() != null) {
                                                    hl7ComplexDataTypeDef.getChildTypes().add(hl7DataTypeDef);
                                                }
                                                complexDataTypeChildElements.putIfAbsent(name, hl7DataTypeDef);

                                            }
//                                                complexDataTypeChildElements.putIfAbsent(ref, hl7DataTypeDef);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //Adds child data types for the complex content data types
                        for (int i = 0; i < complexTypes.getLength(); i++) {
                            NamedNodeMap attributes = complexTypes.item(i).getAttributes();
                            if (attributes.getNamedItem("name") != null) {
                                String complexTypeName = attributes.getNamedItem("name").getNodeValue();
                                if (complexTypeName.endsWith(".CONTENT")) {
                                    NodeList childNodes = complexTypes.item(i).getChildNodes();
                                    for (int j = 0; j < childNodes.getLength(); j++) {
                                        String nodeName = childNodes.item(j).getNodeName();
                                        if ("xsd:complexContent".equals(nodeName) || "xsd:simpleContent".equals(nodeName)) {
                                            NodeList childElems = childNodes.item(j).getChildNodes();
                                            for (int l = 0; l < childElems.getLength(); l++) {
                                                if ("xsd:extension".equals(childElems.item(l).getNodeName())) {
                                                    NamedNodeMap childElemAttributes = childElems.item(l).getAttributes();
                                                    if (childElemAttributes != null &&
                                                            childElemAttributes.getNamedItem("base") != null) {
                                                        String base = childElemAttributes.getNamedItem("base").getNodeValue();
                                                        HL7DataTypeDef hl7DataTypeDef = dataTypesDefMap.get(base);
                                                        if (hl7DataTypeDef != null) {
                                                            complexDataTypeChildElements.get(complexTypeName.substring(0, complexTypeName
                                                                    .indexOf(".CONTENT"))).getChildTypes().add(hl7DataTypeDef);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    hl7Spec.getDataTypeDefinitions().putAll(dataTypesDefMap);

                } catch(SAXException | IOException | ParserConfigurationException e){
                    //handle exception
                }
            }
        }

        private Map<String, HL7FieldDef> parseAndReturnFieldsSchema (String fieldSchemaPath){

            File fieldSchemaFile = new File(fieldSchemaPath);
            if (fieldSchemaFile.isFile() && fieldSchemaFile.getName().endsWith(".xsd")) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                try {
                    Document doc = dbFactory.newDocumentBuilder().parse(fieldSchemaFile);
                    doc.getDocumentElement().normalize();

                    Map<String, HL7FieldDef> fieldDefMap = new LinkedHashMap<>();
                    NodeList complexTypes = doc.getElementsByTagName("xsd:complexType");
                    if (complexTypes != null) {
                        for (int i = 0; i < complexTypes.getLength(); i++) {
                            NamedNodeMap attributes = complexTypes.item(i).getAttributes();
                            if (attributes.getNamedItem("name") != null) {
                                String fieldName = attributes.getNamedItem("name").getNodeValue();
                                if (fieldName.endsWith(".CONTENT")) {
                                    NodeList childNodes = complexTypes.item(i).getChildNodes();
                                    for (int j = 0; j < childNodes.getLength(); j++) {
                                        if ("xsd:simpleContent".equals(childNodes.item(j).getNodeName())
                                                || "xsd:complexContent".equals(childNodes.item(j).getNodeName())) {
                                            NodeList childElements = childNodes.item(j).getChildNodes();
                                            for (int k = 0; k < childElements.getLength(); k++) {
                                                if ("xsd:extension".equals(childElements.item(k).getNodeName())) {
                                                    NamedNodeMap childAttributes = childElements.item(k).getAttributes();
                                                    if (childAttributes != null &&
                                                            childAttributes.getNamedItem("base") != null) {
                                                        String base = childAttributes.getNamedItem("base").getNodeValue();
                                                        HL7DataTypeDef hl7DataTypeDef = new HL7DataTypeDef();
                                                        hl7DataTypeDef.setName(base);
                                                        HL7FieldDef hl7FieldDef = new HL7FieldDef();
                                                        hl7FieldDef.setName(fieldName);
                                                        hl7FieldDef.setBaseDataType(hl7DataTypeDef);
                                                        fieldDefMap.putIfAbsent(fieldName, hl7FieldDef);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Map<String, HL7FieldDef> fieldDefinitions = new LinkedHashMap<>();
                    NodeList elements = doc.getElementsByTagName("xsd:element");
                    if (elements != null) {
                        for (int i = 0; i < elements.getLength(); i++) {
                            NamedNodeMap attributes = elements.item(i).getAttributes();
                            if (attributes.getNamedItem("type") != null) {
                                String fieldName = attributes.getNamedItem("name").getNodeValue();
                                HL7FieldDef fieldDef = fieldDefMap.get(attributes.getNamedItem("type").getNodeValue());
                                fieldDefinitions.putIfAbsent(fieldName, fieldDef);
                            }
                        }
                    }
                    return fieldDefinitions;
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    //handle exception
                }
            }
            return null;
        }
    }
