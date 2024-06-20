package com.example.validation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends Human {
//    private String id;
    public String username;
    public String password;
    public String email;

    public User() {
    }
}
