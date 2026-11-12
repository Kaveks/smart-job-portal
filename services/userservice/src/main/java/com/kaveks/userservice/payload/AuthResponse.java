package com.kaveks.userservice.payload;

import com.kaveks.commonlib.dto.user.UserResponse;
import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private String message;
//    private String title;
    private UserResponse user;
}
