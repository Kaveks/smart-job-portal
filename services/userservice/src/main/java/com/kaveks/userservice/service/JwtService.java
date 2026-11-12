
package com.kaveks.userservice.service;

import com.kaveks.commonlib.domain.UserRole;
import com.kaveks.userservice.config.JwtProvider;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProvider jwtProvider;

    public JwtService(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public String generateAccessToken(
            Long userId,
            UserRole role
    ) {

        return jwtProvider.generateAccessToken(
                userId,
                role
        );
    }
}
