package com.demoProject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpDto {
    private String lastName;
    private String organizationName;
    private String organizationRCNumber;
    private String email;
    private String firstName;
    private String phoneNumber;
    private String password;
}
