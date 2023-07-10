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
 * Holds HL7 message type metadata information.
 */
public class HL7MessageDef implements SpecModel {

        private String name;
        private String description;
        private List<HL7SegmentDef> segments;
        private List<HL7GroupDef> groups;

        public HL7MessageDef(String name) {
            this.name = name;
            this.segments = new ArrayList<>();
            this.groups = new ArrayList<>();
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

        public List<HL7SegmentDef> getSegments() {
            return segments;
        }

        public void setSegments(List<HL7SegmentDef> segments) {
            this.segments = segments;
        }

        public void addSegment(HL7SegmentDef segment) {
            segments.add(segment);
        }

        public HL7SegmentDef getSegment(String name) {
            for (HL7SegmentDef segment : segments) {
                if (segment.getName().equals(name)) {
                    return segment;
                }
            }
            return null;
        }

        public List<HL7GroupDef> getGroups() {
            return groups;
        }

        public void setGroups(List<HL7GroupDef> groups) {
            this.groups = groups;
        }

        public void addGroup(HL7GroupDef group) {
            groups.add(group);
        }

        public HL7GroupDef getGroup(String name) {
            for (HL7GroupDef group : groups) {
                if (group.getName().equals(name)) {
                    return group;
                }
            }
            return null;
        }
}
