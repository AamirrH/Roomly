package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_guests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many BookingGuest records belong to one Booking.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * Many BookingGuest records belong to one Guest.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;
}
