package com.kaveks.userservice.payload;

import com.kaveks.commonlib.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    private String phone;
    @NotBlank(message = "email is required")
    @Email(message = "provide a valid email")
    private String email;
    @NotNull(message = "role is required")
    private UserRole role;
    @NotBlank(message = "password is required")
    private String password;
}
