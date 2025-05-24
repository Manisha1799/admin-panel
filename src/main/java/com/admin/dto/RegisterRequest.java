package com.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO for registration requests
@Data
public class RegisterRequest {

    @Email(message = "Please enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;
}