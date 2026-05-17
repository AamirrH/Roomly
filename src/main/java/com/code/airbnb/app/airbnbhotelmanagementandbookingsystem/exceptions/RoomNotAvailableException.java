package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions;

public class RoomNotAvailableException extends RuntimeException {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}
