package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class HotelManagerControllers {

    private final HotelService hotelService;
    private final RoomService roomService;


    @PreAuthorize("hasAuthority('HOTEL_VIEW')")
    @GetMapping("/roomly/hotels")
    private ResponseEntity<List<HotelResponseDTO>> findAll() {
        return ResponseEntity.ok(hotelService.findAll());
    }


    @PreAuthorize("hasAuthority('HOTEL_CREATE')")
    @PostMapping("/hotels")
    private ResponseEntity<HotelResponseDTO> createHotel(@RequestBody @Valid HotelRequestDTO hotelRequestDTO) {
        return ResponseEntity.ok(hotelService.createHotel(hotelRequestDTO));
    }

    @PreAuthorize("hasAuthority('HOTEL_VIEW')")
    @GetMapping("/hotels/{hotelId}")
    private ResponseEntity<HotelResponseDTO> findHotelById(@PathVariable(name = "hotelId") Long id ){
        return ResponseEntity.ok(hotelService.findById(id));
    }

    // Update a Specific Hotel by id
    @PreAuthorize("hasAuthority('HOTEL_UPDATE')")
    @PatchMapping("/hotels/{hotelId}")
    private ResponseEntity<HotelResponseDTO> updateHotelByHotelId(@PathVariable(name = "hotelId") Long id, @RequestBody Map<String,Object> updates) {
        return hotelService.updateHotelByHotelId(id,updates);
    }

    @PreAuthorize("hasAuthority('HOTEL_DELETE')")
    @DeleteMapping("/hotels/{hotelId}")
    private ResponseEntity<String> deleteHotelByHotelId(@PathVariable(name = "hotelId") Long id) {
        hotelService.deleteHotelByHotelId(id);
        return ResponseEntity.ok("The Specified Hotel has been Deleted");
    }

    // Get All Rooms of a Particular Hotel with Hotel I'd
    @PreAuthorize("hasAuthority('ROOM_VIEW')")
    @GetMapping("/hotels/{hotelId}/rooms")
    private ResponseEntity<List<RoomResponseDTO>> findAllRoomsByHotelId(@PathVariable(name = "hotelId") Long id ){
        return ResponseEntity.ok(roomService.getAllRoomsByHotelId(id));
    }


    // Add a Room to particular hotel with Hotel id
    @PreAuthorize("hasAuthority('ROOM_CREATE')")
    @PostMapping("/hotels/{hotelId}/rooms")
    private ResponseEntity<RoomResponseDTO> addRooms(@RequestBody @Valid RoomRequestDTO room, @PathVariable(name = "hotelId") Long id ){
        return ResponseEntity.ok(roomService.addRoomToHotel(room,id));
    }

    // Get a Particular Room of a Particular Hotel
    @PreAuthorize("hasAuthority('ROOM_VIEW')")
    @GetMapping("/hotels/{hotelId}/rooms/{roomId}")
    private ResponseEntity<RoomResponseDTO> getRoom(@PathVariable Long hotelId,
                                                    @PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoomById(roomId, hotelId));
    }

    @PreAuthorize("hasAuthority('ROOM_UPDATE')")
    @PatchMapping("/hotels/{hotelId}/rooms/{roomId}")
    private ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable Long hotelId,
                                                    @PathVariable Long roomId,
                                                    @RequestBody RoomPatchDTO roomPatchDTO){
        return ResponseEntity.ok(roomService.updateRoom(roomId, hotelId, roomPatchDTO));
    }

    @PreAuthorize("hasAuthority('ROOM_DELETE')")
    @DeleteMapping("/hotels/{hotelId}/rooms/{roomId}")
    private void deleteRoom(@PathVariable Long hotelId, @PathVariable Long roomId){
        roomService.deleteRoom(roomId, hotelId);
        ResponseEntity.ok("Room Successfully deleted!");
    }








}
