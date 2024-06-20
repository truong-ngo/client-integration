package com.example.validation.controller;

import com.example.validation.api_integration.IntegrationExecutor;
import com.example.validation.api_integration.IntegrationStep;
import com.example.validation.client.UserClient;
import com.example.validation.client.UserClientInterface;
import com.example.validation.data.Person;
import com.example.validation.data.User;
import com.example.validation.integration.MethodExecutionConfig;
import com.example.validation.integration.PipelineExecutor;
import com.example.validation.integration_v2.MethodExecutionConfigV2;
import com.example.validation.integration_v2.PipelineExecutorV2;
import com.example.validation.validation.Valid;
import com.example.validation.validation.Validated;
import com.example.validation.validation.ValidationRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PersonController {

    private final UserClientInterface userClient;

    @PostMapping
    @Validated
    public ResponseEntity<Person> createPerson(
            @RequestBody
            @Valid(rule = "classpath:validation-profile.json")
            Person person) {
        log.info("Person {} is created", person);
        return ResponseEntity.ok(person);
    }

    @GetMapping
    public ResponseEntity<User> getUserInfo(@RequestParam String path) {
        User user = User.builder()
                .username("truongnx2.os")
                .build();

//        MethodExecutionConfig config = getPipelineConfig("classpath:change_pass.json");
//        PipelineExecutor executor = new PipelineExecutor();
//        User body = executor.execute(userClient, user, User.class, config);
//        executor.clearCache();
        MethodExecutionConfigV2 config = getPipelineConfig(path);
        PipelineExecutorV2 executor = new PipelineExecutorV2();
        User body = executor.execute(Map.of(UserClient.class, userClient), user, User.class, config);
        Map<String, Object> callMethod = executor.getCache();
        System.out.println(callMethod);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/integration")
    public ResponseEntity<Object> runIntegration(@RequestParam String config) {
        IntegrationStep configuration = getIntegrationStepConfig(config);
        IntegrationExecutor executor = new IntegrationExecutor();
        Map<String, Object> input = Map.of("user", User.builder().username("truong").password("1234556").build(), "departmentCode", "DP001");
        Object response = executor.execute(configuration, input, Object.class);
        return ResponseEntity.ok(response);
    }

    private MethodExecutionConfigV2 getPipelineConfig(String classPath) {
        try (InputStream in = new FileInputStream(ResourceUtils.getFile(classPath))) {
            return new ObjectMapper().readValue(in.readAllBytes(), MethodExecutionConfigV2.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private IntegrationStep getIntegrationStepConfig(String classPath) {
        try (InputStream in = new FileInputStream(ResourceUtils.getFile(classPath))) {
            return new ObjectMapper().readValue(in.readAllBytes(), IntegrationStep.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
