package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.advices;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.advices.customusererros.CustomUserError;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.NoPermissionException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.UserAlreadyExistsException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalUserExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<CustomUserError> handleUserNotFoundException(UserNotFoundException ex) {
        CustomUserError error = new CustomUserError(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<CustomUserError> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        CustomUserError error = new CustomUserError(HttpStatus.CONFLICT, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(NoPermissionException.class)
    private ResponseEntity<CustomUserError> handleNoPermissionException(NoPermissionException ex) {
        CustomUserError error = new CustomUserError(HttpStatus.FORBIDDEN, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<CustomUserError> handleBadCredentialsException(BadCredentialsException ex) {
        CustomUserError error = new CustomUserError(HttpStatus.UNAUTHORIZED, "Invalid email or password", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomUserError> handleAuthenticationException(AuthenticationException e) {
        CustomUserError  authError = new CustomUserError(HttpStatus.UNAUTHORIZED, "Could not be authenticated. Try again later", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<CustomUserError> handleJWTException(JwtException e) {
        CustomUserError  jwtError = new CustomUserError(HttpStatus.UNAUTHORIZED, "JWT could not be verified", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(jwtError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomUserError> handleAccessDeniedException(AccessDeniedException e) {
        CustomUserError accessDeniedError = new CustomUserError(HttpStatus.FORBIDDEN, "You do not have permission to perform this operation", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessDeniedError);
    }


}
