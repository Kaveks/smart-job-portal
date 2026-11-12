package com.kaveks.userservice.service;

import com.kaveks.commonlib.dto.user.UserResponse;
import com.kaveks.userservice.payload.UpdateRequest;

import java.util.List;

public interface UserService {
    UserResponse  getUserByEmail(String email);
    UserResponse getUserById(Long id);
    List<UserResponse> userList();
    UserResponse updateProfile(Long id, UpdateRequest req);
    // admin action
    String suspendUser(Long id);
    String activateUser(Long id);
    String deleteUser(Long id);
}
