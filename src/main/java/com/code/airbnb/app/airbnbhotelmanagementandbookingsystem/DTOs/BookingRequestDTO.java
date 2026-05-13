package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingRequestDTO {

    // This will be caught from the Frontend, when user search for available rooms
    private Long roomId;
    // This will be caught from the Frontend, when user search for available rooms
    private Long hotelId;
    @NotBlank(message = "City is Required!")
    @Size(max = 20,message = "Length cannot be longer than 20 characters")
    private String city;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfRooms;


}
