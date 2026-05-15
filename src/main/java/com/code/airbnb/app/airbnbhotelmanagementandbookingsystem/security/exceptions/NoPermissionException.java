package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions;

public class NoPermissionException extends RuntimeException {
    public NoPermissionException(String message) {
        super(message);
    }
}
