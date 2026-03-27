package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HotelService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class GuestSearchControllers {

    private final HotelService  hotelService;

    @GetMapping("/search")
    private ResponseEntity<Page<HotelResponseDTO>> searchHotels(@RequestParam(required = false) String city,
                                                                @RequestParam(required = false) LocalDate checkInDate,
                                                                @RequestParam(required = false) LocalDate checkOutDate,
                                                                @RequestParam(required = false) Integer numberOfRooms,
                                                                @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                @RequestParam(defaultValue = "10") Integer pageSize){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        return ResponseEntity.ok(hotelService.searchHotels(city,checkInDate,checkOutDate,numberOfRooms,pageable));
    }
//    @GetMapping("/{hotelId}")
//    private ResponseEntity<HotelResponseDTO> getHotelById(@PathVariable Long hotelId,
//                                                          @RequestParam(required = false) LocalDate checkInDate,
//                                                          @RequestParam(required = false) LocalDate checkOutDate,
//                                                          @RequestParam(required = false) Integer numberOfRooms){
//        return ResponseEntity.ok(hotelService.showHotelDetails(hotelId,checkInDate,checkOutDate,numberOfRooms);
//    }



}
