package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
