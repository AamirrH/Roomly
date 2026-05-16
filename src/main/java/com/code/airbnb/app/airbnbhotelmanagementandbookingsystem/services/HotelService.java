package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.InventoryRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;


    public List<HotelResponseDTO> findAll() {
        return hotelRepository.findAll()
                .stream()
                .map(hotel -> modelMapper.map(hotel, HotelResponseDTO.class))
                .collect(Collectors.toList());
    }
    // Create a Hotel and save in Database
    @Transactional
    public HotelResponseDTO createHotel(HotelRequestDTO hotelRequestDTO) {
        Hotel hotel = modelMapper.map(hotelRequestDTO, Hotel.class);
        // wire the bidirectional relationship before saving
        if (hotel.getContactInfo() != null) {
            hotel.getContactInfo().setHotel(hotel);
        }
        Hotel savedHotel = hotelRepository.save(hotel);
        return modelMapper.map(savedHotel,HotelResponseDTO.class);
    }

    public HotelResponseDTO findById(Long id){
        return modelMapper.map(hotelRepository.findHotelById(id),HotelResponseDTO.class);
    }

    public List<HotelResponseDTO> showHotelDetails(Long hotelId,LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfRooms,Integer totaldays) {
        return roomRepository.findAllRoomsByHotelId(hotelId).
                stream()
                .map(room -> modelMapper.map(room, HotelResponseDTO.class))
                .collect(Collectors.toList());
    }


    public ResponseEntity<HotelResponseDTO> updateHotelByHotelId(Long id, Map<String,Object> updates) {
        Hotel hotel = hotelRepository.findHotelById(id);
        updates.forEach((key, value) -> {
            // Find Required Field in Hotel.Class with same value as key and put it in a reference variable
            Field field = ReflectionUtils.findField(Hotel.class, key);
            if(field != null) {
                ReflectionUtils.makeAccessible(field);
                // Set the value of a particular hotel of those fields
                ReflectionUtils.setField(field, hotel, value);
            }
        });
        Hotel savedHotel = hotelRepository.save(hotel);
        return ResponseEntity.ok(modelMapper.map(savedHotel,HotelResponseDTO.class));

    }

    public void deleteHotelByHotelId(Long id) {
        Hotel hotel = hotelRepository.findHotelById(id);
        // Deleting the Hotel
        hotelRepository.delete(hotel);
        return;
    }
}
