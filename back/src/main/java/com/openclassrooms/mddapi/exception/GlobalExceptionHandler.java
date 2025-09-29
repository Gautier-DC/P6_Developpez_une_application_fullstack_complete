package com.openclassrooms.mddapi.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, WebRequest request) {
        
        log.warn("User already exists: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "USER_ALREADY_EXISTS",
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, WebRequest request) {
        
        log.warn("User not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "USER_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            Exception ex, WebRequest request) {

        log.warn("Invalid credentials attempt from: {}", request.getDescription(false));

        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_CREDENTIALS",
            "Email ou mot de passe invalide",
            HttpStatus.UNAUTHORIZED.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex, WebRequest request) {
        
        log.warn("Invalid token: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_TOKEN",
            ex.getMessage(),
            HttpStatus.UNAUTHORIZED.value(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException ex, WebRequest request) {

        // For login requests, treat as invalid credentials (security measure)
        if (request.getDescription(false).contains("/api/auth/login")) {
            log.warn("Authentication attempt with non-existent user from: {}", request.getDescription(false));

            ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_CREDENTIALS",
                "Email ou mot de passe invalide",
                HttpStatus.UNAUTHORIZED.value(),
                request.getDescription(false).replace("uri=", "")
            );

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // For other requests, return 404
        log.warn("Username not found: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "USER_NOT_FOUND",
            "User not found",
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            Exception ex, WebRequest request) {
        
        List<String> validationErrors;
        
        if (ex instanceof MethodArgumentNotValidException) {
            validationErrors = ((MethodArgumentNotValidException) ex).getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        } else {
            validationErrors = ((BindException) ex).getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        }
        
        log.warn("Validation errors: {}", validationErrors);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", ""),
            validationErrors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "BAD_REQUEST",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleArticleNotFound(
            ArticleNotFoundException ex, WebRequest request) {

        log.warn("Article not found: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "ARTICLE_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(
            CommentNotFoundException ex, WebRequest request) {

        log.warn("Comment not found: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "COMMENT_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ThemeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleThemeNotFound(
            ThemeNotFoundException ex, WebRequest request) {

        log.warn("Theme not found: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "THEME_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ThemeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleThemeAlreadyExists(
            ThemeAlreadyExistsException ex, WebRequest request) {

        log.warn("Theme already exists: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "THEME_ALREADY_EXISTS",
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedOperation(
            UnauthorizedOperationException ex, WebRequest request) {

        log.warn("Unauthorized operation: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "UNAUTHORIZED_OPERATION",
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAuthorizationHeader(
            InvalidAuthorizationHeaderException ex, WebRequest request) {

        log.warn("Invalid authorization header: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_AUTHORIZATION_HEADER",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}