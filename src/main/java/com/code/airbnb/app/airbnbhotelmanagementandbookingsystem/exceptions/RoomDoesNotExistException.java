package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions;

public class RoomDoesNotExistException extends RuntimeException {
    public RoomDoesNotExistException(String message) {
        super(message);
    }
}
