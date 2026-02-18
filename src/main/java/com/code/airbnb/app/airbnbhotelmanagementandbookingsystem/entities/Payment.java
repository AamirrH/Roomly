package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String transactionId;

    private BigDecimal price;

    @CreationTimestamp
    private LocalDateTime paymentCreatedAt;

    @UpdateTimestamp
    private LocalDateTime paymentUpdatedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne(mappedBy = "paymentIdForBookedRoom")
    private Booking bookingId;



}
