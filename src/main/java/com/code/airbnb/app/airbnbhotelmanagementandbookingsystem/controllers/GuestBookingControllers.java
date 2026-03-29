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

    // Create A Booking
    @PostMapping("/bookings")
    private ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequestDTO));
    }

    @PostMapping("/{bookingId}/guests")

    private ResponseEntity<BookingResponseDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestDTOList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDTOList));
    }






}
