package com.example.validation.integration_v2;

import com.example.validation.integration.IntegrationPipelineException;
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
public class MethodExecutorV2 {

    private final MethodExecutionConfigV2 config;

    public MethodExecutorV2(MethodExecutionConfigV2 config) {
        this.config = config;
    }

    /**
     * Execute method's tree of integration pipeline
     * @param clients: clients instance
     * @param input: input of integration pipeline
     * @param previousMethodInput: previous method execution result
     * @param methodsInvocationCache: cache stored methods invocation's result in pipeline
     */
    public Object invokeMethod(
            Map<Class<?>, Object> clients,
            Object input,
            Object previousMethodInput,
            Map<String, Object> methodsInvocationCache) {

        try {
            Object clientValue = getClientValue(config, clients);
            Method method = getMethod(config);
            Object result;
            if (method.getParameterCount() > 0) {
                Object[] parameters = extractMethodParameter(method, config.getParamsConfig(), input, previousMethodInput, methodsInvocationCache);
                result = method.invoke(clientValue, parameters);
            } else {
                result = method.invoke(clientValue);
            }

            methodsInvocationCache.put(method.getName(), result);

            List<MethodExecutionConfigV2> nextMethods = config.getNextMethods();
            if (nextMethods == null || nextMethods.size() == 0) {
                return result;
            }

            for (MethodExecutionConfigV2 nextMethod : nextMethods) {
                Boolean condition = extractCondition(nextMethod.getInvokeCondition(), input, result, methodsInvocationCache);
                if (condition) {
                    MethodExecutorV2 nextMethodExecutor = new MethodExecutorV2(nextMethod);
                    return nextMethodExecutor.invokeMethod(clients, input, result, methodsInvocationCache);
                }
            }

            throw new IntegrationPipelineException("Invalid config");

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IntegrationPipelineException("Invalid config");
        }
    }

    /**
     * Get client's {@link Class} type
     * */
    private Class<?> getClientType(MethodExecutionConfigV2 config) {
        try {
            return Class.forName(config.getClientType());
        } catch (ClassNotFoundException exception) {
            throw new IntegrationPipelineException("Invalid config, client type not found: " + config.getClientType());
        }
    }

    /**
     * Get client's value
     * */
    private Object getClientValue(MethodExecutionConfigV2 config, Map<Class<?>, Object> clients) {
        Class<?> clientType = getClientType(config);
        return clients.get(clientType);
    }

    /**
     * Get array of parameter's {@link Class} type
     * */
    private Class<?>[] getParametersType(MethodExecutionConfigV2 config) {
        try {
            Class<?>[] classes = new Class[config.getParametersType().size()];
            for (int i = 0; i < config.getParametersType().size(); i++) {
                classes[i] = Class.forName(config.getParametersType().get(i));
            }
            return classes;
        } catch (ClassNotFoundException exception) {
            throw new IntegrationPipelineException("Invalid config, parameter type not found " + config.getParametersType());
        }
    }

    /**
     * Get {@link Method} from config
     */
    private Method getMethod(MethodExecutionConfigV2 config) {
        try {
            Class<?> clazz = getClientType(config);
            return clazz.getMethod(config.getMethodName(), getParametersType(config));
        } catch (NoSuchMethodException exception) {
            throw new IntegrationPipelineException("Invalid config", exception);
        }
    }

    /**
     * Extract method parameter
     * */
    private Object[] extractMethodParameter(
            Method method,
            List<ParameterConfig> parameterConfigs,
            Object input,
            Object previousMethodResult,
            Map<String, Object> methodsInvocationCache) {

        Object[] parameters = new Object[method.getParameterCount()];
        for (ParameterConfig parameterConfig : parameterConfigs) {
            Object context = parameterConfig.getSource().isFromPipelineInput() ?
                    input : parameterConfig.getSource().isFromPreviousMethod() ?
                            previousMethodResult : methodsInvocationCache;
            parameters[parameterConfig.getParamIndex()] = extractValue(context, parameterConfig.getExtractExpression());
        }
        return parameters;
    }

    /**
     * Extract condition that decide what next method is going to be execute
     * */
    private Boolean extractCondition(InvokeCondition invokeConditions, Object input, Object previousMethodResult, Map<String, Object> methodsInvocationCache) {
        if (invokeConditions == null) {
            return true;
        }
        Map<String, Object> contexts = new HashMap<>();
        for (InputSource source : invokeConditions.getSources()) {
            Object context = source.isFromPipelineInput() ?
                    input : source.isFromPreviousMethod() ? previousMethodResult : methodsInvocationCache;
            contexts.put(source.name(), context);
        }
        return (Boolean) extractValue(contexts, invokeConditions.getExtractExpression());
    }

    /**
     * Extract value using SpEl
     */
    private Object extractValue(Object source, String expression) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(source);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new IntegrationPipelineException("Invalid config", exception);
        }
    }
}
