package com.kaveks.userservice.mapper;

import com.kaveks.commonlib.dto.user.UserResponse;
import com.kaveks.userservice.model.User;

public class UserMapper {
    public static UserResponse toUserResponse(User user){
        UserResponse userResponse= new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());
        userResponse.setStatus(user.getStatus());
        userResponse.setLastLogin(user.getLastLogin());
        userResponse.setCreatedAt(user.getCreatedAt());
        return userResponse;
    }
}
