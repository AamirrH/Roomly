package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import jakarta.persistence.*;

@Entity
public class BookingGuest {
    // Table which basically tells what BookingId is reserved with what GuestId

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingGuestId;

    @ManyToOne
    private Booking bookingId;

    @ManyToOne
    private Guest guestId;


}
