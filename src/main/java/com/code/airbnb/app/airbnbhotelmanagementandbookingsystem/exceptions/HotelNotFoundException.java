package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(String message) {
        super(message);
    }
}
