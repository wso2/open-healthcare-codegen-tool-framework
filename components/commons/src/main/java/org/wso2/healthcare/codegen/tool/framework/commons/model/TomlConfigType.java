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

package org.wso2.healthcare.codegen.tool.framework.commons.model;

import net.consensys.cava.toml.TomlParseResult;
import org.wso2.healthcare.codegen.tool.framework.commons.Constants;

/**
 * Holds tool config in TOML format.
 */
public class TomlConfigType implements ConfigType<TomlParseResult> {

    private TomlParseResult configObj;

    @Override
    public TomlParseResult getConfigObj() {
        return configObj;
    }

    @Override
    public void setConfigObj(TomlParseResult configObj) {
        this.configObj = configObj;
    }

    @Override
    public String getType() {
        return Constants.TOML_CONFIG_TYPE;
    }
}
