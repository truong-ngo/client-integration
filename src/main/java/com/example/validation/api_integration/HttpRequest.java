package com.example.validation.api_integration;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Data
@Builder
public class HttpRequest<T> {
    private String url;
    private MultiValueMap<String, String> headers;
    private MultiValueMap<String, Object> queryParams;
    private Map<String, Object> pathVariables;
    private String method;
    private T body;
}
