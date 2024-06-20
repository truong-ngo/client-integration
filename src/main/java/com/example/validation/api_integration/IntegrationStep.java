package com.example.validation.api_integration;

import lombok.Data;

import java.util.List;

@Data
public class IntegrationStep {
    private ApiConfiguration apiConfig;
    private String callCondition;
    private List<IntegrationStep> nextStep;
}
