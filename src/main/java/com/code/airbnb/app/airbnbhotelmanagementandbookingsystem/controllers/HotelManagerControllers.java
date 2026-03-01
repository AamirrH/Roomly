package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HotelService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.RoomService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class HotelManagerControllers {

    private final HotelService hotelService;
    private final RoomService roomService;


    @GetMapping("/hotels")
    private ResponseEntity<List<Hotel>> findAll() {
        return ResponseEntity.ok(hotelService.findAll());
    }

    @PostMapping("/hotel")
    private ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelService.createHotel(hotel));
    }

    @GetMapping("/hotels/{hotelId}")
    private ResponseEntity<Hotel> findHotelById(@PathVariable(name = "hotelId") Long id ){
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @PatchMapping("/hotels/{hotelId}")
    private ResponseEntity<Hotel> updateHotelByHotelId(@PathVariable(name = "hotelId") Long id, @RequestBody Hotel hotel) {
        return hotelService.updateHotelByHotelId();
    }






}
