package com.example.validation.integration_v2;

import lombok.Data;

import java.util.List;

@Data
public class InvokeCondition {
    private List<InputSource> sources;
    private String extractExpression;
}
