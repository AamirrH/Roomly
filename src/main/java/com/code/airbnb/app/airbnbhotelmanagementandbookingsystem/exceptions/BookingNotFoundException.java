package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}
