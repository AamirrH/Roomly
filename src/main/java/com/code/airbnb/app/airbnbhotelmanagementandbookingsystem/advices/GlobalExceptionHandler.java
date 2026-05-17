package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.advices;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.advices.customerrors.CustomExceptionError;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.BookingExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BookingExpiredException.class)
    private ResponseEntity<CustomExceptionError> handleBookingExpiredException(BookingExpiredException ex) {
        CustomExceptionError bookingExpiredError = new CustomExceptionError(HttpStatus.CONFLICT,ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(bookingExpiredError);
    }



}
