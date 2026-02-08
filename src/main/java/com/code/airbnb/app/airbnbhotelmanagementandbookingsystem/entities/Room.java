package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Room {

    @Id
    private Long roomId;

    @ManyToOne
    private Hotel hotel;

    private String type;

    private BigDecimal basePrice;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // How many rooms of a specific type are in the specific hotel with a specific hotelId
    private Integer totalCount;

    // Capacity of a specific type of room
    private Integer capacity;

}
