package com.example.validation.integration;

import java.util.HashMap;
import java.util.Map;

/**
 * Executor of integrate operation in pipeline
 * */
public class PipelineExecutor {

    /**
     * Store the result of method invocation in pipeline execution process
     * */
    private final Map<String, Object> methodInvocationCache = new HashMap<>();

    /**
     * Clear cache result of method's execution in pipeline
     * */
    public void clearCache() {
        methodInvocationCache.clear();
    }

    /**
     * Get method invocation's result in integration process after execute pipeline
     * @param methodName: method that invoked in integration process
     * */
    public Object getMethodInvocationResult(String methodName) {
        Object result = methodInvocationCache.get(methodName);
        if (result == null) {
            throw new IllegalArgumentException("Method not found in pipeline");
        }
        return result;
    }

    /**
     * Execute pipeline operation define in config file
     * @param client: Integration client(s) (single client or {@link Map} of clients name and clients)
     * @param inputs: Input of pipeline integration process (object or Map of object name, object value)
     * @param returnType: Pipeline integration result type
     * @param config: Indicate how the integration process running
     * */
    public <R> R execute(Object client, Object inputs, Class<R> returnType, MethodExecutionConfig config) {
        MethodExecutor executor = new MethodExecutor(config);
        Object result = executor.invokeMethod(client, inputs, methodInvocationCache);
        return returnType.cast(result);
    }
}
