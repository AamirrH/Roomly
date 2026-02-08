package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invId;

    @ManyToOne
    private Hotel hotelId;

    @ManyToOne
    private Room roomId;

    private LocalDateTime date;

    // Number of Rooms that have been booked
    private Integer bookedCount;

    // Number of total available rooms of a specific type
    private Integer totalCount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Factor by which basePrice will increase, basePrice = basePrice * surgeFactor + basePrice
    private BigDecimal surgeFactor;

    private Boolean closed;

}
