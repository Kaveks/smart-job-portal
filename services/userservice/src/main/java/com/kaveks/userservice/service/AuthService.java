package com.kaveks.userservice.service;

import com.kaveks.userservice.payload.AuthResponse;
import com.kaveks.userservice.payload.LoginRequest;
import com.kaveks.userservice.payload.SignUpRequest;

public interface AuthService {
    AuthResponse signUp(SignUpRequest req);
    AuthResponse login(LoginRequest req);
}
