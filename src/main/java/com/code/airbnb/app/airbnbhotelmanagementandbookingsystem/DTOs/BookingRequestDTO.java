package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.User;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
