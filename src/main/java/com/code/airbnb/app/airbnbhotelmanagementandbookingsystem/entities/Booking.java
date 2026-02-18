package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne // Multiple Bookings may be done for the same Hotel.
    private Hotel hotelId;

    @ManyToOne // A Booking may have Multiple Rooms, 1 for 2 Adults, 1 for Teenagers > 18 etc.
    private Room roomId;

    // A User may create Multiple bookings
    @Column(name = "userId")
    @ManyToOne
    private User userIdForBookedRoom;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private BookingStatus status;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    @OneToOne
    private Payment paymentIdForBookedRoom;

}
