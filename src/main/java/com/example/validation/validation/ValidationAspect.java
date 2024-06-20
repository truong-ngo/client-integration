package com.example.validation.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidationAspect {

    private final ValidationExecutor validationExecutor;
    private final ObjectMapper objectMapper;

    @Before("@annotation(com.example.validation.validation.Validated)")
    public void validate(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Valid validAnnotation) {
                    String classPath = validAnnotation.rule();
                    ValidationRule rule = getRule(classPath);
                    boolean check = validationExecutor.validate(rule, args[i]);
                    if (!check) {
                        throw new ValidationException(rule.collectDescription());
                    }
                }
            }
        }
    }

    private ValidationRule getRule(String classPath) {
        try (InputStream in = new FileInputStream(ResourceUtils.getFile(classPath))) {
            return objectMapper.readValue(in.readAllBytes(), ValidationRule.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
