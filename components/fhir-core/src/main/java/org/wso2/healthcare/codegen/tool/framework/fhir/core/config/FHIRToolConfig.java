package org.wso2.healthcare.codegen.tool.framework.fhir.core.config;

import com.google.gson.JsonElement;
import org.wso2.healthcare.codegen.tool.framework.commons.config.AbstractToolConfig;
import org.wso2.healthcare.codegen.tool.framework.commons.exception.CodeGenException;
import org.wso2.healthcare.codegen.tool.framework.commons.model.ConfigType;

public class FHIRToolConfig extends AbstractToolConfig {
    @Override
    public void configure(ConfigType<?> configObj) throws CodeGenException {

    }

    @Override
    public void overrideConfig(String jsonPath, JsonElement value) {

    }
}
