package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.CreatePaymentOrderRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.CreatePaymentOrderResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.PaymentResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.VerifyPaymentRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roomly/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAuthority('BOOKING_CREATE')")
    @PostMapping("/orders")
    public ResponseEntity<CreatePaymentOrderResponseDTO> createPaymentOrder(
            @RequestBody @Valid CreatePaymentOrderRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(paymentService.createPaymentOrder(requestDTO));
    }

    @PreAuthorize("hasAuthority('BOOKING_CREATE')")
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponseDTO> verifyPayment(
            @RequestBody @Valid VerifyPaymentRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(paymentService.verifyPayment(requestDTO));
    }
}
