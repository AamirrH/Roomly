package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions;

public class HotelNotActiveException extends RuntimeException {
    public HotelNotActiveException(String message) {
        super(message);
    }
}
