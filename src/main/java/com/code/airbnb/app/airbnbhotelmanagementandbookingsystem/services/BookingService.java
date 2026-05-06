package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingRequestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.GuestDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.*;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.BookingExpiredException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.BookingNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.RoomDoesNotExistException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final InventoryService inventoryService;
    private final PricingService pricingService;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;


    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
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

        long totalDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
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
        // TODO : Remove Dummy User and ADD Global Exception Handler
        User user = new User();
        user.setId(1L);
        // Dynamic Pricing Strategy
        BigDecimal finalStrategicPrice = BigDecimal.ZERO;
        for(Inventory inventory : availableRooms) {
            finalStrategicPrice = finalStrategicPrice.add(pricingService.calculateFinalPrice(inventory));
        }
        finalStrategicPrice = finalStrategicPrice.multiply(BigDecimal.valueOf(numberOfRooms));

        // Create the actual booking
        Booking booking = Booking.builder()
                .status(BookingStatus.RESERVED)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .hotel(hotel)
                .room(room)
                .user(user)
                .finalCalculatedPrice(finalStrategicPrice)
                .roomsCount(numberOfRooms).build();
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingResponseDTO.class);

    }

    @Transactional
    public BookingResponseDTO addGuests(Long bookingId,List<GuestDTO> guestDTOList) {

        //Find the Booking
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new BookingNotFoundException("Booking with id " + bookingId + " does not exist"));

        // Check if booking has expired or not, and whether the booking is RESERVED or not
        if(isBookingExpired(booking)){
            throw new BookingExpiredException("Booking is expired");
        }
        if(!booking.getStatus().equals(BookingStatus.RESERVED)){
            throw new BookingExpiredException("Booking is not RESERVED");
        }
        List<Guest> guestList = guestDTOList.stream()
                .map(guestDTO -> modelMapper.map(guestDTO, Guest.class))
                .toList();
        for(Guest guest : guestList){
            guest.setUser(getCurrentUser());
        }
        // Save All guests in a single DB call
        guestRepository.saveAll(guestList);
        booking.getGuests().addAll(guestList);

        // Now update booking parameters and save booking
        booking.setStatus(BookingStatus.GUESTS_ADDED);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingResponseDTO.class);
    }

    @Transactional
    public void cancelBooking(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new BookingNotFoundException("Booking with id " + bookingId + " does not exist"));
        // Collect all the inventories related to this booking
        Long hotelId = booking.getHotel().getId();
        Long roomId = booking.getRoom().getId();
        LocalDate checkInDate = booking.getCheckInDate();
        LocalDate checkOutDate = booking.getCheckOutDate();
        Integer numberOfRooms = booking.getRoomsCount();

        List<Inventory> bookedInventories = inventoryRepository
                .findBookedInventoriesAndLockThem(hotelId,roomId,checkInDate,checkOutDate);

        // Cancel Logic
        for(Inventory inventory : bookedInventories){
            inventory.setBookedCount(inventory.getBookedCount() - numberOfRooms);
        }
        // Saving the cancelled Inventories
        inventoryRepository.saveAll(bookedInventories);

        // Setting booking status to cancelled
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // TODO : Handle Payment Refund too

    }

    // Helper Method
    public boolean isBookingExpired(Booking booking) {
        return booking.getCheckOutDate().isBefore(LocalDate.now());

    }

    // Helper Method
    public User getCurrentUser(){
        // TODO : Dummy User Logic
        User user = new User();
        user.setId(1L);
        userRepository.save(user);
        return user;
    }

    public List<BookingResponseDTO> getAllBookings(Long userId){
        return bookingRepository.findByUser_Id(userId)
                .stream()
                .map(bookingDTO -> modelMapper.map(bookingDTO,BookingResponseDTO.class))
                .collect(Collectors.toList());
    }

    public BookingResponseDTO getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->
                new BookingNotFoundException("Booking with the booking id "+bookingId+" not found"));
        return modelMapper.map(booking, BookingResponseDTO.class);
    }


}
