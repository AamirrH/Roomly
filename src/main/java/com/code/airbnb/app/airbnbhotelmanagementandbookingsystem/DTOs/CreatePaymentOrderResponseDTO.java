package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreatePaymentOrderResponseDTO {
    private Long bookingId;
    private Long paymentId;
    private String razorpayOrderId;
    private String razorpayKeyId;
    private BigDecimal amount;
    private Long amountInPaise;
    private String currency;
    private String status;
}
