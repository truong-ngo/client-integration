package com.example.validation.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Person extends User {
//    private Long id;
    public String name;
    public String code;
    public String email;
    public String phone;
    public String address;
    public String salary;
}
