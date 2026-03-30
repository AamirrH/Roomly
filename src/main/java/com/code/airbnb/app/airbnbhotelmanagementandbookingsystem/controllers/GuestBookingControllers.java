package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.GuestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GuestBookingControllers {

    private final BookingService bookingService;

    // View all bookings of a specific user
    // TODO : Remove {userId} PathVariable during Auth
    @GetMapping("/bookings/{userId}")
    private ResponseEntity<List<BookingResponseDTO>> getAllBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getAllBookings(userId));
    }

    // View details of a specific booking
    @GetMapping("/booking/{bookingId}")
    private ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }

    // Create A Booking
    @PostMapping("/bookings")
    private ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequestDTO));
    }

    // Add Guests to a existing booking
    @PostMapping("/bookings/{bookingId}/guests")
    private ResponseEntity<BookingResponseDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestDTOList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDTOList));
    }

    // Cancel A Booking
    @PatchMapping("bookings/{bookingId}/cancel")
    private ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking with id " + bookingId + " has been cancelled");
    }





}
