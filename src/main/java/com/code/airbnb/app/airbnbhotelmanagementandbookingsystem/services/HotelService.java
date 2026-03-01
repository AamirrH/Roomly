package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;




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

//    @Transactional
//    public void deleteById(Long id){
//        hotelRepository.deleteHotelById(id);
//    }
//
//
//    public Hotel updateHotelByHotelId(Long id, Hotel hotel) {
//
//        Hotel oldHotel = hotelRepository.getHotelById(id);
//
//
//
//
//    }
}
