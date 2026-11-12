
package com.kaveks.commonweb.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.net.URI;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException exception
    ) throws IOException, ServletException {

        // Log the complete exception and its cause chain.
        // Never log the Authorization header or raw JWT.
        log.warn(
                "Authentication failed for {} {}. Exception type: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getName(),
                exception
        );

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String code;
        String detail;
        String challenge = "Bearer";

        if (exception instanceof OAuth2AuthenticationException oauthException) {

            String errorCode = oauthException.getError().getErrorCode();
            String description = oauthException.getError().getDescription();

            if ("invalid_request".equals(errorCode)) {

                code = "INVALID_AUTHORIZATION_REQUEST";
                detail = "The Authorization header is malformed or contains an invalid Bearer token request.";
                challenge = "Bearer error=\"invalid_request\"";

            } else if ("invalid_token".equals(errorCode)) {

                challenge = "Bearer error=\"invalid_token\"";

                switch (description == null ? "" : description) {

                    case "Access token has expired." -> {
                        code = "TOKEN_EXPIRED";
                        detail = "Your access token has expired. Please authenticate again.";
                    }

                    case "Access token is malformed." -> {
                        code = "MALFORMED_TOKEN";
                        detail = "The supplied access token is malformed.";
                    }

                    case "Access token could not be verified." -> {
                        code = "TOKEN_VERIFICATION_FAILED";
                        detail = "The supplied access token could not be verified.";
                    }

                    case "JWT is missing required claims." -> {
                        code = "MISSING_REQUIRED_CLAIMS";
                        detail = "The supplied access token is missing required claims.";
                    }

                    case "JWT does not contain valid roles." -> {
                        code = "INVALID_TOKEN_ROLES";
                        detail = "The supplied access token does not contain valid role information.";
                    }

                    case "Unsupported JWT signing algorithm." -> {
                        code = "UNSUPPORTED_SIGNING_ALGORITHM";
                        detail = "The supplied access token uses an unsupported signing algorithm.";
                    }

                    default -> {
                        code = "INVALID_BEARER_TOKEN";
                        detail = "The supplied access token is invalid.";
                    }
                }

            } else {

                code = "AUTHENTICATION_FAILED";
                detail = "The supplied authentication credentials could not be accepted.";
            }

        } else if (
                exception instanceof InsufficientAuthenticationException
                        || exception instanceof AuthenticationCredentialsNotFoundException
        ) {

            code = "AUTHENTICATION_REQUIRED";
            detail = "Authentication credentials were not provided. Supply a valid Bearer access token.";

        } else {

            code = "AUTHENTICATION_FAILED";
            detail = "Authentication failed. Check the supplied credentials.";
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                detail
        );

        problem.setType(URI.create("about:blank"));
        problem.setTitle("Unauthorized");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, challenge);

        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
