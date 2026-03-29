package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.*;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.RoomDoesNotExistException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.HotelRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.InventoryRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final InventoryService inventoryService;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public BookingResponseDTO createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        // Getting Values from Request Body
        Long hotelId = bookingRequestDTO.getHotelId();
        Hotel hotel = hotelRepository.findHotelById(hotelId);

        Long roomId = bookingRequestDTO.getRoomId();
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RoomDoesNotExistException("Room with the following id " +roomId+ " does not exist"));

        String city = bookingRequestDTO.getCity();
        LocalDate checkInDate = bookingRequestDTO.getCheckInDate();
        LocalDate checkOutDate = bookingRequestDTO.getCheckOutDate();
        Integer numberOfRooms = bookingRequestDTO.getNumberOfRooms();

        // Check if Rooms are available for these dates.
        List<Inventory> availableRooms = inventoryRepository.findAvailableInventoriesAndLockThem(hotelId, roomId, checkInDate, checkOutDate, numberOfRooms);

        long totalDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate) + 1;
        // Check if the rooms are available or not
        if(availableRooms.size() != totalDays){
            throw new RoomDoesNotExistException("Rooms not available for specified dates");
        }

        // Reserve the rooms
        for(Inventory inventory : availableRooms){
            // Update Booking Count
            inventory.setBookedCount(inventory.getBookedCount() + numberOfRooms);
        }

        inventoryRepository.saveAll(availableRooms);
        // TODO : Remove Dummy User
        User user = new User();
        user.setId(1L);

        // Create the actual booking
        Booking booking = Booking.builder()
                .status(BookingStatus.RESERVED)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .hotel(hotel)
                .room(room)
                .user(user)
                .roomsCount(numberOfRooms).build();

        return modelMapper.map(booking, BookingResponseDTO.class);

    }

}
