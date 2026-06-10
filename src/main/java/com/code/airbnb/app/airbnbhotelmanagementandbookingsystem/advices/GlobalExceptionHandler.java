package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.advices;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.advices.customerrors.CustomExceptionError;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
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
        CustomExceptionError hotelNotActiveError = new CustomExceptionError(HttpStatus.CONFLICT,ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(hotelNotActiveError);
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

    @ExceptionHandler(RoomNotAvailableException.class)
    private ResponseEntity<CustomExceptionError> handleRoomNotAvailableException(RoomNotAvailableException ex) {
        CustomExceptionError roomNotAvailableError = new CustomExceptionError(HttpStatus.CONFLICT, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(roomNotAvailableError);
    }

    @ExceptionHandler(PaymentException.class)
    private ResponseEntity<CustomExceptionError> handlePaymentException(PaymentException ex) {
        CustomExceptionError paymentError = new CustomExceptionError(HttpStatus.BAD_REQUEST, ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(paymentError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<CustomExceptionError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        CustomExceptionError validationError = new CustomExceptionError(HttpStatus.BAD_REQUEST, message, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<CustomExceptionError> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Invalid request");

        CustomExceptionError validationError = new CustomExceptionError(HttpStatus.BAD_REQUEST, message, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<CustomExceptionError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value for " + ex.getName();
        if (ex.getRequiredType() != null) {
            message += ". Expected " + ex.getRequiredType().getSimpleName();
        }

        CustomExceptionError validationError = new CustomExceptionError(HttpStatus.BAD_REQUEST, message, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
    }

    // Handles all Generic Exceptions
    @ExceptionHandler(Exception.class)
    private ResponseEntity<CustomExceptionError> handleGenericException(Exception ex) {
        log.error("Unhandled application exception", ex);
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
