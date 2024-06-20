package com.example.validation.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Configure the method's execution tree in the integration pipeline
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodExecutionConfig {

    /**
     * Indicate integration client
     * */
    private String clientName;

    /**
     * Indicate the execution method in the integration pipeline, result of root method invocation will be the output of integration pipeline
     * */
    private String methodName;

    /**
     * Indicate array of parameter(s) type. Example: java.lang.String
     * */
    private List<String> parametersType;

    /**
     * Indicate parameters that come from pipeline input(s)<br/>
     * Key: index of parameter<br/>
     * Value: Spel expression use to get parameter from input(s)
     * */
    private Map<Integer, String> paramsFromPipelineInputs;

    /**
     * Indicate parameter that come from dependency method(s)<br/>
     * Key: index of parameter<br/>
     * Value: Spel expression use to get parameter from dependency method(s) call result
     * */
    private Map<Integer, String> paramsFromDependencyMethods;

    /**
     * Dependency methods
     * */
    private List<MethodExecutionConfig> dependencyMethods;

}
