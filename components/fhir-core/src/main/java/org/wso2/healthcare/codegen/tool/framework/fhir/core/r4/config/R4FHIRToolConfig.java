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

package org.wso2.healthcare.codegen.tool.framework.fhir.core.r4.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.consensys.cava.toml.TomlArray;
import net.consensys.cava.toml.TomlParseResult;
import net.consensys.cava.toml.TomlTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.healthcare.codegen.tool.framework.commons.Constants;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.commons.model.ConfigType;
import org.wso2.healthcare.codegen.tool.framework.commons.model.TomlConfigType;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.config.FHIRToolConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds config information applicable for all the FHIR tools to be used.
 */
public class R4FHIRToolConfig extends FHIRToolConfig {

    private static final Log LOG = LogFactory.getLog(R4FHIRToolConfig.class);
    private final Map<String, R4IGConfig> igConfigs = new HashMap<>();
    private final Map<String, R4DataTypeConfig> dataTypeConfigs = new HashMap<>();
    private final List<String> dataTypeProfileDirs = new ArrayList<>();
    private final List<String> terminologyDirs = new ArrayList<>();

    @Override
    public void configure(ConfigType<?> configObj) throws CodeGenException {
        this.setConfigObj(configObj);
        String type = configObj.getType();
        if (Constants.JSON_CONFIG_TYPE.equals(type)) {
            JsonObject jsonConfigObj = (JsonObject) configObj.getConfigObj();
            JsonArray resourceProfiles = jsonConfigObj.getAsJsonArray("FHIRImplementationGuides");
            if (resourceProfiles != null) {
                for (JsonElement resourceProfile : resourceProfiles) {
                    R4IGConfig r4IgConfig = new R4IGConfig(resourceProfile.getAsJsonObject());
                    igConfigs.put(r4IgConfig.getName(), r4IgConfig);
                }
            }
            JsonObject metadataConfigObj = jsonConfigObj.getAsJsonObject("metadata");
            if (metadataConfigObj != null) {
                JsonArray dataTypeStructures = metadataConfigObj.getAsJsonArray("dataTypeStructures");
                for (JsonElement dataTypeStructure : dataTypeStructures) {
                    R4DataTypeConfig dataTypeConfig = new R4DataTypeConfig(dataTypeStructure.getAsJsonObject());
                    dataTypeConfigs.put(dataTypeConfig.getName(), dataTypeConfig);
                }
            }
            JsonArray dataTypeProfiles = jsonConfigObj.getAsJsonArray("dataTypeProfiles");
            if (dataTypeProfiles != null) {
                for (JsonElement dataTypeProfile : dataTypeProfiles) {
                    dataTypeProfileDirs.add(dataTypeProfile.getAsJsonObject().get("dirPath").getAsString());
                }
            }
            JsonArray terminologies = jsonConfigObj.getAsJsonArray("terminologies");
            if (terminologies != null) {
                for (JsonElement terminology : terminologies) {
                    terminologyDirs.add(terminology.getAsJsonObject().get("dirPath").getAsString());
                }
            }
        } else if (Constants.TOML_CONFIG_TYPE.equals(type)) {
            TomlParseResult tomlConfigObj = ((TomlConfigType) configObj.getConfigObj()).getConfigObj();
            Object implementationGuides = tomlConfigObj.get("implementation_guides");
            if (implementationGuides instanceof TomlArray) {
                List<Object> implementationGuidesList = ((TomlArray) implementationGuides).toList();
                for (Object implementationGuide : implementationGuidesList) {
                    if (implementationGuide instanceof TomlTable) {
                        R4IGConfig r4IgConfig = new R4IGConfig((TomlTable) implementationGuide);
                        igConfigs.put(r4IgConfig.getName(), r4IgConfig);
                    }
                }
            }
            Object metadata = tomlConfigObj.get("metadata");
            if (metadata instanceof TomlTable) {
                TomlArray dataTypeStructures = ((TomlTable) metadata).getArray("data_type_structures");
                if (dataTypeStructures != null) {
                    List<Object> dataTypeStructuresList = dataTypeStructures.toList();
                    for (Object dataTypeStructure : dataTypeStructuresList) {
                        R4DataTypeConfig dataTypeConfig = new R4DataTypeConfig((TomlTable) dataTypeStructure);
                        dataTypeConfigs.put(dataTypeConfig.getName(), dataTypeConfig);
                    }
                }
            }
            Object dataTypeProfiles = tomlConfigObj.get("data_type_profiles");
            if (dataTypeProfiles instanceof TomlArray) {
                List<Object> dataTypeProfileList = ((TomlArray) dataTypeProfiles).toList();
                for (Object dataTypeProfile : dataTypeProfileList) {
                    dataTypeProfileDirs.add(((TomlTable) dataTypeProfile).getString("dir_path"));
                }
            }
            Object terminologies = tomlConfigObj.get("data_type_profiles");
            if (terminologies instanceof TomlArray) {
                List<Object> terminologyList = ((TomlArray) terminologies).toList();
                for (Object terminology : terminologyList) {
                    terminologyDirs.add(((TomlTable) terminology).getString("dir_path"));
                }
            }
        } else {
            throw new CodeGenException("Unsupported tool configuration format: " + type);
        }
    }

    @Override
    public void overrideConfig(String jsonPath, JsonElement value) {
        if (jsonPath.equals("FHIRImplementationGuides")) {
            R4IGConfig r4IgConfig = new R4IGConfig(value.getAsJsonObject());
            igConfigs.put(r4IgConfig.getName(), r4IgConfig);
        } else {
            LOG.warn("Invalid config path: " + jsonPath);
        }
    }

    public Map<String, R4IGConfig> getIgConfigs() {
        return igConfigs;
    }

    public Map<String, R4DataTypeConfig> getDataTypeConfigs() {
        return dataTypeConfigs;
    }

    public List<String> getDataTypeProfileDirs() {
        return dataTypeProfileDirs;
    }

    public List<String> getTerminologyDirs() {
        return terminologyDirs;
    }
}
