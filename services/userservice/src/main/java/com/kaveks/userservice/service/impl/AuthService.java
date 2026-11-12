package com.kaveks.userservice.service.impl;

import com.kaveks.commonlib.domain.UserRole;
import com.kaveks.commonlib.domain.UserStatus;
import com.kaveks.userservice.mapper.UserMapper;
import com.kaveks.userservice.model.User;
import com.kaveks.userservice.payload.AuthResponse;
import com.kaveks.userservice.payload.LoginRequest;
import com.kaveks.userservice.payload.SignUpRequest;
import com.kaveks.userservice.repository.UserRepository;
import com.kaveks.userservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService implements com.kaveks.userservice.service.AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse signUp(SignUpRequest req) {
        String email = normalizeEmail(req.getEmail());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }
        if (req.getRole() == UserRole.ROLE_ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Administrator accounts cannot be created through public registration"
            );
        }
        User user = new User();

        user.setFirstName(req.getFirstName().trim());
        user.setLastName(req.getLastName().trim());
        user.setEmail(email);
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        user.setRole(req.getRole());
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                savedUser.getId(),
                savedUser.getRole()
        );
        return buildAuthResponse(
                savedUser,
                accessToken,
                "Account created successfully"
        );
    }
    @Override
    @Transactional
    public AuthResponse login(LoginRequest req) {

        String email = normalizeEmail(req.getEmail());

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "No account found with this email address"
                ));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Incorrect password"
            );
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your account is not active"
            );
        }

        user.setLastLogin(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
                updatedUser.getId(),
                updatedUser.getRole()
        );

        return buildAuthResponse(
                updatedUser,
                accessToken,
                "Login successful"
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private AuthResponse buildAuthResponse(
            User user,
            String accessToken,
            String message

    ) {

        AuthResponse response=new AuthResponse();
        response.setAccessToken(accessToken);
        response.setTokenType("Bearer");
        response.setMessage(message);
        response.setUser(UserMapper.toUserResponse(user));

        return response;
    }
}