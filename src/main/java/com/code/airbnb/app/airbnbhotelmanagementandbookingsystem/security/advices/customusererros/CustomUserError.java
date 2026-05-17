package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.advices.customusererros;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomUserError {

    private HttpStatus status;
    private String message;
    private LocalDateTime createdAt;

    public CustomUserError(HttpStatus status, String message, LocalDateTime createdAt) {
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CustomUserError{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
