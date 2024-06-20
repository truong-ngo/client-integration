package com.example.validation.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRule {
    // Atomic profile
    private String fieldName;
    private String expression;
    private String description;

    // Composite profile
    private CombineType combineType;
    List<ValidationRule> rules;

    public Map<String, String> collectDescription() {
        Map<String, String> result = new HashMap<>();
        if (rules == null) {
            result.put(fieldName, description);
        } else {
            for (ValidationRule profile : rules) {
                result.putAll(profile.collectDescription());
            }
        }
        return result;
    }
}
