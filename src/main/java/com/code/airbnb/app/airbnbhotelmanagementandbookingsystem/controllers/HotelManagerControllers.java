package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HotelService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.RoomService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
    private ResponseEntity<List<HotelResponseDTO>> findAll() {
        return ResponseEntity.ok(hotelService.findAll());
    }


    @PostMapping("/hotel")
    private ResponseEntity<HotelResponseDTO> createHotel(@RequestBody @Valid HotelRequestDTO hotelRequestDTO) {
        return ResponseEntity.ok(hotelService.createHotel(hotelRequestDTO));
    }

    @GetMapping("/hotels/{hotelId}")
    private ResponseEntity<HotelResponseDTO> findHotelById(@PathVariable(name = "hotelId") Long id ){
        return ResponseEntity.ok(hotelService.findById(id));
    }

//    @PatchMapping("/hotels/{hotelId}")
//    private ResponseEntity<HotelResponseDTO> updateHotelByHotelId(@PathVariable(name = "hotelId") Long id, @RequestBody HotelRequestDTO hotelRequestDTO) {
//        return hotelService.updateHotelByHotelId();
//    }

    // Get All Rooms of a Particular Hotel with Hotel Id
    @GetMapping("/hotels/{hotelId}/rooms")
    private ResponseEntity<List<RoomResponseDTO>> findAllRoomsByHotelId(@PathVariable(name = "hotelId") Long id ){
        return ResponseEntity.ok(roomService.getAllRoomsByHotelId(id));
    }

    // Add a Room to particular hotel with Hotel id
    @PostMapping("/hotels/{hotelId}/rooms")
    private ResponseEntity<RoomResponseDTO> addRooms(@RequestBody @Valid RoomRequestDTO room, @PathVariable(name = "hotelId") Long id ){
        return ResponseEntity.ok(roomService.addRoomToHotel(room,id));
    }




}
