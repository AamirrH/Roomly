package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    private List<Room> getAllRoomsByHotelId(Long id){
        return roomRepository.findAllRoomsByHotelId(id);
    }

    private Room addRoomToHotel(Room room, Long hotelId){
        Hotel hotel = hotelRepository.getHotelById(hotelId);
        room.setHotel(hotel);
        return roomRepository.save(room);
    }

}
