package com.example.validation.integration;

public class IntegrationPipelineException extends RuntimeException {

    public IntegrationPipelineException(String message) {
        super(message);
    }

    public IntegrationPipelineException(String message, Throwable cause) {
        super(message, cause);
    }
}
