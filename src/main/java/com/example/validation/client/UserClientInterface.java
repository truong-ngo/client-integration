package com.example.validation.client;

import com.example.validation.data.User;

import java.util.Map;

public interface UserClientInterface {
    Map<String, String> getToken(User user);
    User getUserInfo(String token);
    User changePassword(User user);
}
