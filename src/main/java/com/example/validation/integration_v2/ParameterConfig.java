package com.example.validation.integration_v2;

import lombok.Data;

/**
 * Configure for method parameter
 * */
@Data
public class ParameterConfig {

    /**
     * Indicate parameter index
     * */
    private Integer paramIndex;

    /**
     * Parameter value's source
     * */
    private InputSource source;

    /**
     * SpEl expression for extracting method value
     * */
    private String extractExpression;
}
