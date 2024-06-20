package com.example.validation.controller;

import com.example.validation.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleException(ValidationException e) {
        return new ResponseEntity<>(e.getMessages(), HttpStatus.BAD_REQUEST);
    }
}
