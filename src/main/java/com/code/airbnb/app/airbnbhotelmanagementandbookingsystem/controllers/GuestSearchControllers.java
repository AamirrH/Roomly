package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HotelService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class GuestSearchControllers {

    private final HotelService  hotelService;

    @GetMapping("/search")
    private ResponseEntity<HotelResponseDTO> searchHotels(@RequestParam(required = false) String city,
                                                          @RequestParam(required = false) LocalDate checkInDate,
                                                          @RequestParam(required = false) LocalDate checkOutDate,
                                                          @RequestParam(required = false) Integer numberOfRooms){
        return ResponseEntity.ok(hotelService.searchHotels(city,checkInDate,checkOutDate,numberOfRooms));
    }
//    @GetMapping("/{hotelId}")
//    private ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long hotelId,
//                                                          @RequestParam(required = false) LocalDate checkInDate,
//                                                          @RequestParam(required = false) LocalDate checkOutDate,
//                                                          @RequestParam(required = false) Integer numberOfRooms){
//        return ResponseEntity.ok(hotelService.showHotelDetails(hotelId,checkInDate,checkOutDate,numberOfRooms);
//    }



}
