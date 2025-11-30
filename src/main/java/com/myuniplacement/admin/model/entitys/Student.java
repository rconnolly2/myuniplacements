package com.myuniplacement.admin.model.entitys;

import lombok.Data;

@Data
public class Student {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dateOfBirth;
    private String profileImageUrl;
    private String cvFileUrl;
}