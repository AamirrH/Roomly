package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.*;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.HotelNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.RoomDoesNotExistException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final PricingService pricingService;

    @Transactional
    public InventoryResponseDTO updateInventory(Long hotelId, Long roomId, LocalDate date, InventoryPatchDTO inventoryPatchDTO) {
        Inventory inventory = inventoryRepository.findByHotel_IdAndRoom_IdAndDate(hotelId, roomId, date)
                .orElseThrow(() -> new RoomDoesNotExistException("Inventory does not exist for hotel " + hotelId + ", room " + roomId + " on " + date));

        ModelMapper patchMapper = new ModelMapper();
        patchMapper.getConfiguration().setSkipNullEnabled(true);
        patchMapper.map(inventoryPatchDTO, inventory);

        if (inventory.getBookedCount() > inventory.getTotalCount()) {
            throw new IllegalArgumentException("Booked count cannot be greater than total count");
        }

        return mapInventory(inventoryRepository.save(inventory));
    }

    public void initialiseRoomForAYear(Room room){
        LocalDate dateToday = LocalDate.now();
        LocalDate dateEnd = dateToday.plusYears(1);

        List<Inventory> inventoryList = new ArrayList<>();

        while(!dateToday.isAfter(dateEnd)) {
            Inventory inventory = new Inventory();
            inventory.setRoom(room);
            inventory.setHotel(room.getHotel());
            inventory.setBookedCount(0);
            inventory.setTotalCount(room.getTotalCount());
            inventory.setSurgeFactor(BigDecimal.valueOf(1));
            inventory.setDate(dateToday);
            inventoryList.add(inventory);
            dateToday = dateToday.plusDays(1);
        }

        // Single-DB call is efficient
        inventoryRepository.saveAll(inventoryList);
    }
    public void deleteByRoomIdAndHotelId(Long roomId, Long hotelId) {
        // Deletes all rows with the specified roomId and HotelId
        inventoryRepository.deleteByRoom_IdAndHotel_Id(roomId, hotelId);
    }
     //Customer Service Method
    public Page<HotelResponseDTO> searchHotels(String city, LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfRooms, Pageable pageable) {
        validateSearchDates(checkInDate, checkOutDate);
        Long totalDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return inventoryRepository.findAvailableHotels(city, checkInDate, checkOutDate, numberOfRooms, totalDays, pageable)
                .map(hotel -> mapHotelWithEstimatedStartingPrice(hotel, checkInDate, checkOutDate, numberOfRooms));
    }


    public List<RoomResponseDTO> getAvailableRoomsForHotel(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfRooms) {
        validateSearchDates(checkInDate, checkOutDate);
        Long totalDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return inventoryRepository.findAvailableRoomsForHotel(hotelId, checkInDate, checkOutDate, numberOfRooms, totalDays)
                .stream()
                .map(room -> mapRoomWithEstimatedPrice(room, hotelId, checkInDate, checkOutDate, numberOfRooms, totalDays))
                .collect(Collectors.toList());
    }

    private void validateSearchDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    private RoomResponseDTO mapRoomWithEstimatedPrice(Room room, Long hotelId, LocalDate checkInDate,
                                                      LocalDate checkOutDate, Integer numberOfRooms, Long totalDays) {
        RoomResponseDTO roomResponseDTO = modelMapper.map(room, RoomResponseDTO.class);
        List<Inventory> availableInventories = inventoryRepository.findAvailableInventoriesForRoom(
                hotelId,
                room.getId(),
                checkInDate,
                checkOutDate,
                numberOfRooms
        );

        BigDecimal estimatedTotalPrice = availableInventories.stream()
                .map(pricingService::calculateFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(numberOfRooms));

        roomResponseDTO.setEstimatedTotalPrice(estimatedTotalPrice);
        if (totalDays > 0) {
            roomResponseDTO.setEstimatedAverageNightlyPrice(
                    estimatedTotalPrice.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
            );
        }

        return roomResponseDTO;
    }

    private HotelResponseDTO mapHotelWithEstimatedStartingPrice(com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel hotel,
                                                                LocalDate checkInDate,
                                                                LocalDate checkOutDate,
                                                                Integer numberOfRooms) {
        HotelResponseDTO hotelResponseDTO = modelMapper.map(hotel, HotelResponseDTO.class);

        getAvailableRoomsForHotel(hotel.getId(), checkInDate, checkOutDate, numberOfRooms)
                .stream()
                .map(RoomResponseDTO::getEstimatedTotalPrice)
                .filter(price -> price != null)
                .min(Comparator.naturalOrder())
                .ifPresent(hotelResponseDTO::setEstimatedStartingPrice);

        return hotelResponseDTO;
    }

    private InventoryResponseDTO mapInventory(Inventory inventory) {
        return InventoryResponseDTO.builder()
                .id(inventory.getId())
                .hotelId(inventory.getHotel().getId())
                .roomId(inventory.getRoom().getId())
                .date(inventory.getDate())
                .bookedCount(inventory.getBookedCount())
                .totalCount(inventory.getTotalCount())
                .surgeFactor(inventory.getSurgeFactor())
                .closed(inventory.getClosed())
                .build();
    }
}
