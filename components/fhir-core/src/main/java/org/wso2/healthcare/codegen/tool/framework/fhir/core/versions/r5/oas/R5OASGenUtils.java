package org.wso2.healthcare.codegen.tool.framework.fhir.core.versions.r5.oas;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.hl7.fhir.r5.model.SearchParameter;
import org.wso2.healthcare.codegen.tool.framework.fhir.core.oas.OASGenUtils;

public class R5OASGenUtils extends OASGenUtils {
    /**
     * Checks whether the parameter is already added to the operation.
     *
     * @param param     search parameter
     * @param operation OAS operation
     * @return true if parameter is added
     */
    protected static boolean isAdded(SearchParameter param, Operation operation){
        if(operation.getParameters() != null){
            for(Parameter parameter : operation.getParameters()){
                if(parameter.getName().equals(param.getCode())){
                    return true;
                }
            }
        }
        return false;
    }
}
