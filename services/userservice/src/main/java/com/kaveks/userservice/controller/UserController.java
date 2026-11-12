package com.kaveks.userservice.controller;

import com.kaveks.commonlib.domain.UserRole;
import com.kaveks.commonlib.dto.user.UserResponse;
import com.kaveks.commonweb.security.annotation.AllowedRoles;
import com.kaveks.userservice.payload.UpdateRequest;
import com.kaveks.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserResponse> getUserByEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUser(){
        return ResponseEntity.ok(userService.userList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequest request,
            Authentication authentication
    ){
        String authenticatedUserId = authentication.getName();

        if (!String.valueOf(id).equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "You can only update your own profile."
            );
        }

        return ResponseEntity.ok(userService.updateProfile(id,request));
    }
    // Suspend user
    @PatchMapping("/{id}/suspend")
    @AllowedRoles({UserRole.ROLE_ADMIN})
    public ResponseEntity<String> suspendUser(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.suspendUser(id));
    }

    // Activate user
    @PatchMapping("/{id}/activate")
    @AllowedRoles({UserRole.ROLE_ADMIN})
    public ResponseEntity<String> activateUser(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    // Soft-delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

}
