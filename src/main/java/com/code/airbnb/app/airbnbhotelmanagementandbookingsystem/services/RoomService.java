package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.HotelNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
            roomToAdd.setHotel(hotel);
            roomRepository.save(roomToAdd);
            return modelMapper.map(roomToAdd, RoomResponseDTO.class);
        }
        else{
            throw new HotelNotFoundException("The Hotel with the following id : "+hotelId+" "+"does not exist, Register your hotel first!");
        }
    }

}
