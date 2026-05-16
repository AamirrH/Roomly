package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ManagerBookingResponseDTO {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private Long roomId;
    private String roomType;
    private Long userId;
    private String guestEmail;
    private BookingStatus status;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;
    private BigDecimal finalCalculatedPrice;
    private LocalDateTime createdAt;
}

