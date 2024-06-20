package com.example.validation.client;

import com.example.validation.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class UserClient implements UserClientInterface {

    public Map<String, String> getToken(User user) {
        log.info("get token for user {}", user);
        return Map.of("token", UUID.randomUUID().toString());
    }

    public User getUserInfo(String token) {
        log.info("get user with id: {}", token);
        return User.builder()
//                .id(token)
                .username("truongnx2.os")
                .password("123456")
                .email("truongnx2@mbbank.com.vn")
                .build();
    }

    public boolean validatePassword(User user) {
        log.info("validate user: {}", user);
        return (user.getPassword().equals("123456"));
    }

    @Override
    public User changePassword(User user) {
        log.info("change user {}", user.getUsername());
        user.setPassword("Thinh@011221");
        return user;
    }

    public void tokenWarning(User user) {
        log.info("cannot get token for user: {}", user);
    }

    public void passwordWarning(User user) {
        log.info("invalid password for user: {}", user);
    }
}
