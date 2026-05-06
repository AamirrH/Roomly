package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.User;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
