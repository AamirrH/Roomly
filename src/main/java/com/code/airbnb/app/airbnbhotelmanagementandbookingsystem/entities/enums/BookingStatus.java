package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums;

public enum BookingStatus {
    /** The booking is created but payment is not yet verified. */
    PENDING,

    /** Payment is successful; the room is officially reserved. */
    CONFIRMED,

    /** The guest has arrived and is currently in the room. */
    CHECKED_IN,

    /** The guest has departed and the room is being cleared. */
    CHECKED_OUT,

    /** The guest did not arrive by the cutoff time. */
    NO_SHOW,

    /** The user or admin cancelled the reservation before check-in. */
    CANCELLED,

    /** The system released the room because payment timed out. */
    EXPIRED,

    /** There was an issue with the booking (e.g., overbooking or maintenance). */
    FAILED
}
