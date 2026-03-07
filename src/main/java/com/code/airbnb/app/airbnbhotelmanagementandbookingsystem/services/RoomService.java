package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomPatchDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.HotelNotActiveException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.HotelNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    // Getting all rooms that belong to a specific hotel
    public List<RoomResponseDTO> getAllRoomsByHotelId(Long hotelId){
        return roomRepository.findAllRoomsByHotelId(hotelId)
                .stream()
                .map(room -> modelMapper.map(room, RoomResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Adding a Room, which must belong to a hotel.

    public RoomResponseDTO addRoomToHotel(RoomRequestDTO room, Long hotelId){
        // Check if hotelId is valid, Hotel exists or not
        if(hotelRepository.existsById(hotelId)){
            // Hotel exists, hence add the room.
            Hotel hotel = hotelRepository.findHotelById(hotelId);
            Room roomToAdd = modelMapper.map(room, Room.class);
            // Set hotel to the room
            roomToAdd.setHotel(hotel);
            roomRepository.save(roomToAdd);
            // Initialize Inventory for a Year
            if(hotel.getActive()){
                // Hotel is active, Initialize Inventory with the room
                inventoryService.initialiseRoomForAYear(roomToAdd);
            }
            else{
                throw new HotelNotActiveException("Hotel with hotel id " + hotelId + " is not active");
            }
            return modelMapper.map(roomToAdd, RoomResponseDTO.class);
        }
        else{
            throw new HotelNotFoundException("The Hotel with the following id : "+hotelId+" "+"does not exist, Register your hotel first!");
        }
    }

    // Getting a Room by providing a Hotel id and a Room id

    public RoomResponseDTO getRoomById(Long roomId, Long  hotelId){
        // First we have to find Hotel
        Room room = roomRepository.findByIdAndHotelId(roomId,hotelId).orElseThrow(() ->
                new HotelNotFoundException("Either Hotel id or Room id is Wrong."));
        return modelMapper.map(room, RoomResponseDTO.class);
    }

    public RoomResponseDTO updateRoom(Long roomId, Long hotelId, RoomPatchDTO roomPatchDTO) {
        Room room = roomRepository.findByIdAndHotelId(roomId,hotelId).orElseThrow(() ->
                new HotelNotFoundException("Either Hotel id or Room id is Wrong."));

        // Only maps non-null fields
        ModelMapper patchMapper = new ModelMapper();
        // This is global so we create a seperate instance of model mapper.
        patchMapper.getConfiguration().setSkipNullEnabled(true);
        patchMapper.map(roomPatchDTO, room);

        Room savedRoom = roomRepository.save(room);

        // Inventory Configurations - only totalCount can be changed.


        return modelMapper.map(savedRoom, RoomResponseDTO.class);
    }

    @Transactional
    public void deleteRoom(Long roomId, Long hotelId) {
        // Finding the Room of the Specific Hotel
        Room room = roomRepository.findByIdAndHotelId(roomId,hotelId).orElseThrow(() ->
                new HotelNotFoundException("Either Hotel id or Room id is Wrong."));

        // Delete all the inventories of this room too.
        inventoryService.deleteByRoomIdAndHotelId(roomId, hotelId);

        // Delete The Room.
        roomRepository.delete(room);

    }
}
