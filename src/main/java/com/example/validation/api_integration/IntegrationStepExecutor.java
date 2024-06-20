package com.example.validation.api_integration;

import com.example.validation.integration.IntegrationPipelineException;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntegrationStepExecutor {

    private final IntegrationStep configuration;

    public IntegrationStepExecutor(IntegrationStep configuration) {
        this.configuration = configuration;
    }

    public ResponseEntity<Object> callApi(Map<String, Object> sources, Map<String, Object> cache) {
        HttpRequest<Object> request = toHttpRequest(configuration.getApiConfig(), sources);
         ResponseEntity<Object> response = DynamicApiClient.callApi(request);
        cache.put(configuration.getApiConfig().getApiName(), response);
        if (configuration.getNextStep() != null && !configuration.getNextStep().isEmpty()) {
            for (IntegrationStep nextStep : configuration.getNextStep()) {
                if (nextStep.getCallCondition() == null || (Boolean) extractValue(nextStep.getCallCondition(), sources)) {
                    IntegrationStepExecutor nextStepExecutor = new IntegrationStepExecutor(nextStep);
                    return nextStepExecutor.callApi(sources, cache);
                }
            }
        }
        return response;
    }

    public HttpRequest<Object> toHttpRequest(ApiConfiguration configuration, Map<String, Object> sources) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        if (configuration.getHeaders() != null) {
            configuration.getHeaders().forEach((k, v) -> headers.put(k, List.of((String) extractValue(v, sources))));
        }

        MultiValueMap<String, Object> queryParams = new LinkedMultiValueMap<>();
        if (configuration.getQueryParams() != null) {
            configuration.getQueryParams().forEach((k, v) -> queryParams.put(k, List.of(extractValue(v, sources))));
        }

        Map<String, Object> pathVariables = new HashMap<>();
        if (configuration.getPathVariables() != null) {
            configuration.getPathVariables().forEach((k, v) -> pathVariables.put(k, extractValue(v, sources)));
        }

        Map<String, Object> body = new HashMap<>();
        if (configuration.getBody() != null) {
            configuration.getBody().forEach((k, v) -> body.put(k, extractValue(v, sources)));
        }

        return HttpRequest.builder()
                .url(configuration.getUrl())
                .method(configuration.getMethod())
                .headers(headers)
                .queryParams(queryParams)
                .pathVariables(pathVariables)
                .body(body)
                .build();
    }

    public Object extractValue(String expression, Map<String, Object> sources) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            return exp.getValue(sources);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new IntegrationPipelineException("Invalid config", exception);
        }
    }

}
