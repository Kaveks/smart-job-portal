package com.kaveks.userservice.service.impl;

import com.kaveks.commonlib.domain.UserStatus;
import com.kaveks.commonlib.dto.user.UserResponse;
import com.kaveks.userservice.mapper.UserMapper;
import com.kaveks.userservice.model.User;
import com.kaveks.userservice.payload.UpdateRequest;
import com.kaveks.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;


import static com.kaveks.userservice.mapper.UserMapper.toUserResponse;

@Service
@RequiredArgsConstructor
public class UserService implements com.kaveks.userservice.service.UserService {
    private final UserRepository userRepository;
    @Override
    @Transactional
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
        return toUserResponse(user);

    }
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    @Override
    @Transactional
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);

        return toUserResponse(user);
    }

    @Override
    @Transactional
    public List<UserResponse> userList() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse updateProfile(Long id, UpdateRequest req) {
        User user = findUserById(id);
        if(req.getFirstName() != null){
            user.setFirstName(req.getFirstName());
        }
        if(req.getLastName() != null){
            user.setLastName(req.getLastName());
        }
        if(req.getPhone() != null){
            user.setPhone(req.getPhone());
        }
        if(req.getProfileImage() != null){
            user.setProfileImage(req.getProfileImage());
        }

        return toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public String suspendUser(Long id) {
        User user =findUserById(id);
        user.setStatus(UserStatus.SUSPENDED);
        user.setSuspendedAt(LocalDateTime.now());
        User updatedUser =userRepository.save(user);
        return "user "+ updatedUser.getFirstName() + " suspended";
    }

    @Override
    public String activateUser(Long id) {
        User user =findUserById(id);
        user.setStatus(UserStatus.ACTIVE);
        user.setSuspendedAt(null);
        User updatedUser =userRepository.save(user);
        return "user "+ updatedUser.getFirstName() + " activated";
    }

    @Override
    public String deleteUser(Long id) {
        User user =findUserById(id);
        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        return "user deleted";
    }
}
