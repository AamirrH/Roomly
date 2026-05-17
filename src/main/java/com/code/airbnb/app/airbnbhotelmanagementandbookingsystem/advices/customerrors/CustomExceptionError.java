package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.advices.customerrors;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CustomExceptionError {

    private HttpStatus httpStatus;

    private String message;

    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "BookingExpiredError{" +
                "httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
