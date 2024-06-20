package com.example.validation.api_integration;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class DynamicApiClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static ResponseEntity<Object> callApi(HttpRequest<Object> request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getUrl());
        if (request.getPathVariables() != null) {
            builder.uriVariables(request.getPathVariables());
        }
        if (request.getQueryParams() != null) {
            request.getQueryParams().forEach((key, values) -> values.forEach(value -> builder.queryParam(key, value)));
        }
        URI uri = builder.build().toUri();
        HttpHeaders headers = request.getHeaders() != null ? new HttpHeaders(request.getHeaders()) : new HttpHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(request.getBody(), headers);
        return restTemplate.exchange(uri, HttpMethod.valueOf(request.getMethod()), entity, Object.class);
    }

}
