package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.advices;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.advices.customerrors.CustomExceptionError;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.*;
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

    @ExceptionHandler(BookingNotFoundException.class)
    private ResponseEntity<CustomExceptionError> handleBookingNotFoundException(BookingNotFoundException ex) {
        CustomExceptionError bookingNotFoundError = new CustomExceptionError(HttpStatus.NOT_FOUND,ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bookingNotFoundError);
    }

    @ExceptionHandler(HolidayAPIException.class)
    private ResponseEntity<CustomExceptionError> handleHolidayAPIException(HolidayAPIException ex) {
        CustomExceptionError holidayAPIExceptionError = new CustomExceptionError(HttpStatus.SERVICE_UNAVAILABLE,ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(holidayAPIExceptionError);
    }

    @ExceptionHandler(HotelNotActiveException.class)
    private ResponseEntity<CustomExceptionError> handleHotelNotActiveException(HotelNotActiveException ex) {
        CustomExceptionError hotelNotActiveError = new CustomExceptionError(HttpStatus.SERVICE_UNAVAILABLE,ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(hotelNotActiveError);
    }

    @ExceptionHandler(HotelNotFoundException.class)
    private ResponseEntity<CustomExceptionError> handleHotelNotFoundException(HotelNotFoundException ex) {
        CustomExceptionError hotelNotFoundError = new CustomExceptionError(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(hotelNotFoundError);
    }

    @ExceptionHandler(RoomDoesNotExistException.class)
    private ResponseEntity<CustomExceptionError> handleRoomDoesNotExistException(RoomDoesNotExistException ex) {
        CustomExceptionError roomDoesNotExistError = new CustomExceptionError(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(roomDoesNotExistError);
    }

    // Handles all Generic Exceptions
    @ExceptionHandler(Exception.class)
    private ResponseEntity<CustomExceptionError> handleGenericException(Exception ex) {
        CustomExceptionError error = new CustomExceptionError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Bad Input Exception Handler
    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<CustomExceptionError> handleIllegalArgumentException(IllegalArgumentException ex) {
        CustomExceptionError error = new CustomExceptionError(HttpStatus.BAD_REQUEST, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }




}
