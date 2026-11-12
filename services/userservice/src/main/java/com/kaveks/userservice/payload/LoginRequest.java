package com.kaveks.userservice.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "provide a valid email")
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message="password is required")
    private String password;
}
