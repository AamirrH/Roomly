package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.GuestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/roomly/api/v1")
@RequiredArgsConstructor
public class GuestBookingControllers {

    private final BookingService bookingService;

    // View all bookings of a specific user
    @PreAuthorize("hasAuthority('BOOKING_VIEW')")
    @GetMapping("/bookings")
    private ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // View details of a specific booking
    @PreAuthorize("hasAuthority('BOOKING_VIEW')")
    @GetMapping("/booking/{bookingId}")
    private ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    // Create A Booking
    @PreAuthorize("hasAuthority('BOOKING_CREATE')")
    @PostMapping("/bookings")
    private ResponseEntity<BookingResponseDTO> createBooking(@RequestBody @Valid BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequestDTO));
    }

    // Add Guests to an existing booking
    @PreAuthorize("hasAuthority('BOOKING_UPDATE')")
    @PatchMapping("/bookings/{bookingId}/guests")
    private ResponseEntity<BookingResponseDTO> addGuests(@PathVariable Long bookingId, @RequestBody @NotEmpty List<@Valid GuestDTO> guestDTOList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDTOList));
    }

    // Cancel A Booking
    @PreAuthorize("hasAuthority('BOOKING_CANCEL')")
    @PatchMapping("bookings/{bookingId}/cancel")
    private ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking with id " + bookingId + " has been cancelled");
    }





}
