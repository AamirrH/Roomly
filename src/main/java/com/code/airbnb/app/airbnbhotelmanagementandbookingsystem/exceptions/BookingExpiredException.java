package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions;

public class BookingExpiredException extends RuntimeException {
    public BookingExpiredException(String message) {
        super(message);
    }
}
