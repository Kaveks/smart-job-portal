package com.kaveks.commonlib.dto.user;

import com.kaveks.commonlib.domain.UserRole;
import com.kaveks.commonlib.domain.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private Boolean verified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
