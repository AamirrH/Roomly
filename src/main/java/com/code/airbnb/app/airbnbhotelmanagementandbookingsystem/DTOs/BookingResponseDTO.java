package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingResponseDTO {

    private Long id;
    private Long hotelId;
    private Long roomId;
    private Long userId;
    private BookingStatus status;
    private LocalDate checkInDate;
    private LocalDate checkoutDate;
    private String city;
    private Integer numberOfRooms;
    private BigDecimal finalCalculatedPrice;

}
