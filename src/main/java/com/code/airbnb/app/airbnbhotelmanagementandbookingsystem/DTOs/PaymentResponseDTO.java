package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentResponseDTO {
    private Long paymentId;
    private Long bookingId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus paymentStatus;
    private BookingStatus bookingStatus;
    private String razorpayOrderId;
    private String razorpayPaymentId;
}
