package com.example.validation.validation;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ValidationExecutor {

    public boolean validate(ValidationRule rule, Object context) {
        boolean result;
        if (rule.getExpression() != null) {
            result = parseExpression(context, rule.getExpression());
        } else {
            result = rule.getCombineType().equals(CombineType.AND) ?
                    andValidate(rule.getRules(), context) :
                    orValidate(rule.getRules(), context);
        }

        return result;
    }

    public boolean parseExpression(Object context, String expression) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            Boolean value = exp.getValue(context, Boolean.class);
            return Boolean.TRUE.equals(value);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new ValidationException(Map.of("error", "error expression string format"), exception);
        }
    }

    public boolean andValidate(List<ValidationRule> profiles, Object context) {
        List<Boolean> list = profiles.stream().map(p -> validate(p, context)).toList();
        return list.stream().allMatch(b -> b.equals(true));
    }

    public boolean orValidate(List<ValidationRule> profiles, Object context) {
        List<Boolean> list = profiles.stream().map(p -> validate(p, context)).toList();
        return list.stream().anyMatch(b -> b.equals(true));
    }
}
