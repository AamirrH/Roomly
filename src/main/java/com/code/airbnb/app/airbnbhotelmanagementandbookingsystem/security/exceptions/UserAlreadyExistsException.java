package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
