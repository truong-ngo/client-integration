package com.example.validation.integration_v2;

public enum InputSource {
    PIPELINE_INPUT, PREVIOUS_METHOD, METHOD_CACHE;

    public boolean isFromPipelineInput() {
        return this.equals(PIPELINE_INPUT);
    }

    public boolean isFromPreviousMethod() {
        return this.equals(PREVIOUS_METHOD);
    }
}
