package com.example.validation.api_integration;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class IntegrationExecutor {
    public final Map<String, Object> apiCache = new HashMap<>();
    public final Map<String, Object> sources = new HashMap<>();

    public <R> R execute(IntegrationStep configuration, Object input, Class<R> clazz) {
        sources.put(ValueSource.INTEGRATION_INPUT.name(), input);
        sources.put(ValueSource.API_CACHE.name(), apiCache);
        IntegrationStepExecutor executor = new IntegrationStepExecutor(configuration);
        ResponseEntity<Object> response = executor.callApi(sources, apiCache);
        return clazz.cast(response.getBody());
    }
}
