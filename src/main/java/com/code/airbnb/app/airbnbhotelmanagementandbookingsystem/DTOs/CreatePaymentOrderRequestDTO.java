package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentOrderRequestDTO {
    @NotNull(message = "Booking id is required")
    private Long bookingId;
}
