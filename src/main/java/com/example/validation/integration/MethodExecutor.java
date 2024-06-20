package com.example.validation.integration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public record MethodExecutor(MethodExecutionConfig config) {

    /**
     * Execute method's tree of integration pipeline
     * @param client: client instance
     * @param inputs: input of integration pipeline
     * @param methodsInvocationCache: cache stored methods invocation's result in pipeline
     */
    public Object invokeMethod(Object client, Object inputs, Map<String, Object> methodsInvocationCache) {
        try {
            /* ==== If method is already invoked, return result from cache ==== */
            if (methodsInvocationCache.containsKey(config.getMethodName())) {
                log.info("get method: " + config.getMethodName() + " invocation result from cache");
                return methodsInvocationCache.get(config.getMethodName());
            }

            /* ==== Get root method of tree ==== */
            Method root = getMethod(config, client);

            /* ==== Execute dependency method ==== */
            List<MethodExecutionConfig> dependencyMethodsConfig = config.getDependencyMethods();
            Map<String, Object> dependencyMethodsInvocationResult = new HashMap<>();
            if (!(dependencyMethodsConfig == null || dependencyMethodsConfig.size() == 0)) {
                for (MethodExecutionConfig c : config.getDependencyMethods()) {
                    MethodExecutor executor = new MethodExecutor(c);
                    dependencyMethodsInvocationResult.put(c.getMethodName(), executor.invokeMethod(client, inputs, methodsInvocationCache));
                }
            }

            /* ==== Build parameters and invoke method ==== */
            Object methodInvocationResult;
            if (root.getParameterCount() > 0) {
                Object[] parameters = buildMethodParameters(config, dependencyMethodsInvocationResult, inputs, client);
                methodInvocationResult = root.invoke(client, parameters);
            } else {
                methodInvocationResult = root.invoke(client);
            }

            /* ==== Cache result ==== */
            methodsInvocationCache.put(config.getMethodName(), methodInvocationResult);

            return methodInvocationResult;
        } catch (InvocationTargetException | IllegalAccessException exception) {
            exception.printStackTrace();
            throw new IntegrationPipelineException(
                    "Error occur while execution method " + config.getMethodName() + " of client " + config.getClientName(),
                    exception);
        }
    }

    /**
     * Get array of parameter {@link Class}
     */
    private Class<?>[] getParametersType(MethodExecutionConfig config) {
        try {
            Class<?>[] classes = new Class[config.getParametersType().size()];
            for (int i = 0; i < config.getParametersType().size(); i++) {
                classes[i] = Class.forName(config.getParametersType().get(i));
            }
            return classes;
        } catch (ClassNotFoundException exception) {
            throw new IntegrationPipelineException("Invalid config", exception);
        }
    }

    /**
     * Get {@link Method} from config
     */
    private Method getMethod(MethodExecutionConfig config, Object client) {
        try {
            Class<?> clazz = client.getClass();
            return clazz.getMethod(config.getMethodName(), getParametersType(config));
        } catch (NoSuchMethodException exception) {
            throw new IntegrationPipelineException("Invalid config", exception);
        }
    }

    /**
     * Build method's parameters
     */
    private Object[] buildMethodParameters(
            MethodExecutionConfig config,
            Map<String, Object> dependencyMethodsInvocationResult,
            Object inputs,
            Object client) {

        Method method = getMethod(config, client);
        if (method.getParameterCount() == 0) {
            return null;
        }

        Object[] parameters = new Object[method.getParameterCount()];

        if (!(config.getParamsFromPipelineInputs() == null || config.getParamsFromPipelineInputs().isEmpty())) {
            for (Integer i : config.getParamsFromPipelineInputs().keySet()) {
                parameters[i] = extractValue(inputs, config.getParamsFromPipelineInputs().get(i));
            }
        }

        if (!(config.getParamsFromDependencyMethods() == null || config.getParamsFromDependencyMethods().isEmpty())) {
            for (Integer i : config.getParamsFromDependencyMethods().keySet()) {
                parameters[i] = extractValue(dependencyMethodsInvocationResult, config.getParamsFromDependencyMethods().get(i));
            }
        }

        return parameters;
    }

    /**
     * Extract value using SpEl
     */
    private Object extractValue(Object context, String expression) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(context);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new IntegrationPipelineException("Invalid config", exception);
        }
    }
}
