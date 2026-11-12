
package com.kaveks.commonweb.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final URI PROBLEM_TYPE = URI.create("about:blank");

    // Handles DTO field validation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        List<Map<String, String>> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage() == null
                                ? "Invalid value"
                                : error.getDefaultMessage()
                ))
                .toList();

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more fields contain invalid values.",
                request
        );

        problem.setProperty("errors", errors);

        return buildResponse(HttpStatus.BAD_REQUEST, problem);
    }

    // Handles constraint validation failures
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {

        List<Map<String, String>> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .toList();

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more request parameters are invalid.",
                request
        );

        problem.setProperty("errors", errors);

        return buildResponse(HttpStatus.BAD_REQUEST, problem);
    }

    // Handles malformed JSON, unsupported fields and invalid JSON values
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        Throwable cause = exception;

        while (cause != null) {

            // Unsupported JSON property
            if (cause instanceof UnrecognizedPropertyException unknownProperty) {

                String field = unknownProperty.getPropertyName();

                return buildInvalidBodyResponse(
                        request,
                        field,
                        "The field '" + field
                                + "' is not supported in this request."
                );
            }

            // Invalid JSON value, including invalid enum values
            if (cause instanceof InvalidFormatException invalidFormat) {

                String field = getFieldPath(invalidFormat);

                Class<?> targetType = invalidFormat.getTargetType();

                String detail;

                if (targetType != null && targetType.isEnum()) {

                    String acceptedValues = Arrays.stream(targetType.getEnumConstants())
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));

                    detail = String.format(
                            "Invalid value for field '%s'. Accepted values are: %s.",
                            field,
                            acceptedValues
                    );

                } else {

                    String expectedType = targetType == null
                            ? "the expected data type"
                            : targetType.getSimpleName();

                    detail = String.format(
                            "Invalid value for field '%s'. Expected type: %s.",
                            field,
                            expectedType
                    );
                }

                return buildInvalidBodyResponse(
                        request,
                        field,
                        detail
                );
            }

            cause = cause.getCause();
        }

        // Malformed JSON, missing body or another unreadable request
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                createProblem(
                        HttpStatus.BAD_REQUEST,
                        "Invalid request body",
                        "The request body is missing, malformed, or contains invalid data.",
                        request
                )
        );
    }

    // Extracts the nested JSON field path
    private String getFieldPath(InvalidFormatException exception) {

        String field = exception.getPath()
                .stream()
                .map(JacksonException.Reference::getPropertyName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));

        return field.isBlank() ? "unknown" : field;
    }

    // Builds a field-specific invalid request response
    private ResponseEntity<ProblemDetail> buildInvalidBodyResponse(
            HttpServletRequest request,
            String field,
            String message) {

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more request fields are invalid.",
                request
        );

        problem.setProperty(
                "errors",
                List.of(Map.of(
                        "field", field,
                        "message", message
                ))
        );

        return buildResponse(HttpStatus.BAD_REQUEST, problem);
    }


    // Handles method-level authorization failures
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {

        log.warn(
                "Access denied for {} {}. Exception type: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );

        return buildResponse(
                HttpStatus.FORBIDDEN,
                createProblem(
                        HttpStatus.FORBIDDEN,
                        "You are not authorised!",
                        exception.getMessage(),
                        request
                )
        );
    }

    // Handles authentication failures reaching MVC exception handling
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationFailure(
            AuthenticationException exception,
            HttpServletRequest request) {

        log.warn(
                "Authentication failed for {} {}. Exception type: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                createProblem(
                        HttpStatus.UNAUTHORIZED,
                        "Authentication required",
                        "Authentication is required or the supplied credentials are invalid.",
                        request
                )
        );
    }

    // Handles missing endpoints
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                createProblem(
                        HttpStatus.NOT_FOUND,
                        "Resource not found",
                        "The requested resource could not be found.",
                        request
                )
        );
    }

    // Handles unsupported HTTP methods
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                createProblem(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "Method not allowed",
                        "The HTTP method is not supported for this endpoint.",
                        request
                )
        );
    }

    // Handles unsupported content types
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                createProblem(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "Unsupported media type",
                        "The request content type is not supported.",
                        request
                )
        );
    }

    // Handles application exceptions with explicit HTTP status codes
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request) {

        HttpStatusCode status = exception.getStatusCode();

        String detail = exception.getReason() == null
                ? "The request could not be processed."
                : exception.getReason();

        ProblemDetail problem = createProblem(
                status,
                resolveTitle(status),
                detail,
                request
        );

        return buildResponse(status, problem);
    }

    // Handles unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        log.error(
                "Unexpected exception while processing request: {} {}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                createProblem(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal server error",
                        "An unexpected error occurred while processing your request.",
                        request
                )
        );
    }

    // Creates a standardized Problem Detail response
    private ProblemDetail createProblem(
            HttpStatusCode status,
            String title,
            String detail,
            HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                detail
        );

        problem.setType(PROBLEM_TYPE);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }

    // Builds the HTTP response
    private ResponseEntity<ProblemDetail> buildResponse(
            HttpStatusCode status,
            ProblemDetail problem) {

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    // Resolves a readable title for an HTTP status
    private String resolveTitle(HttpStatusCode status) {

        HttpStatus httpStatus = HttpStatus.resolve(status.value());

        return httpStatus == null
                ? "Request failed"
                : httpStatus.getReasonPhrase();
    }
}
