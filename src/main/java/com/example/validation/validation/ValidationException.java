package com.example.validation.validation;

import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, String> messages;

    public ValidationException(Map<String, String> messages) {
        this.messages = messages;
    }

    public ValidationException(Map<String, String> messages, Throwable cause) {
        super(cause);
        this.messages = messages;
    }

    public Map<String, String> getMessages() {
        return messages;
    }
}
