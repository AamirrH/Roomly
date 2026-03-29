package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.controllers;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.BookingService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HotelService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.InventoryService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class GuestSearchControllers {

    private final HotelService  hotelService;
    private final InventoryService inventoryService;
    private final RoomService roomService;
    private final BookingService bookingService;

    @GetMapping("/search")
    private ResponseEntity<Page<HotelResponseDTO>> searchHotels(@RequestParam(required = false) String city,
                                                                @RequestParam(required = false) LocalDate checkInDate,
                                                                @RequestParam(required = false) LocalDate checkOutDate,
                                                                @RequestParam(required = false) Integer numberOfRooms,
                                                                @RequestParam(defaultValue = "0") Integer pageNumber,
                                                                @RequestParam(defaultValue = "10") Integer pageSize){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        return ResponseEntity.ok(inventoryService.searchHotels(city,checkInDate,checkOutDate,numberOfRooms,pageable));
    }

    // Search a specific hotel with given parameters
    @GetMapping("/{hotelId}")
    private ResponseEntity<List<RoomResponseDTO>> getHotels(@RequestParam(required = false) LocalDate checkInDate,
                                                            @RequestParam(required = false) LocalDate checkOutDate,
                                                            @RequestParam(required = false) Integer numberOfRooms,
                                                            @PathVariable Long hotelId){
        return ResponseEntity.ok(inventoryService.getAvailableRoomsForHotel(hotelId, checkInDate, checkOutDate, numberOfRooms));

    }

    // Get All Static Details of a Specific Room of a Specific Hotel
    @GetMapping("/{hotelId}/rooms/{roomId}")
    private ResponseEntity<RoomResponseDTO> getGuestRoomDetails(@PathVariable Long hotelId, @PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoomById(roomId,hotelId));
    }









}
