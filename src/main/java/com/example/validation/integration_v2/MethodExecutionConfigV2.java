package com.example.validation.integration_v2;

import lombok.Data;

import java.util.List;

/**
 * Configure method that invoked in integration's pipeline
 * */
@Data
public class MethodExecutionConfigV2 {

    /**
     * Indicate client's type that have invoked method.<br/>
     * Example: com.example.client.T24Client
     * */
    private String clientType;

    /**
     * Indicate method name
     * */
    private String methodName;

    /**
     * Indicate list of method's parameter type
     * */
    private List<String> parametersType;

    /**
     * Invocation condition;
     * */
    private InvokeCondition invokeCondition;

    /**
     * Config for parameters
     * */
    private List<ParameterConfig> paramsConfig;

    /**
     * Indicate next method in integration's pipeline
     * */
    private List<MethodExecutionConfigV2> nextMethods;
}
