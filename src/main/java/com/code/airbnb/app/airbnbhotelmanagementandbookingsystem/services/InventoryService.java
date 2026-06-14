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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Page<com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel> hotelPage =
                inventoryRepository.findAvailableHotels(city, checkInDate, checkOutDate, numberOfRooms, totalDays, pageable);
        Map<Long, BigDecimal> startingPrices = findStartingPricesForHotels(
                hotelPage.getContent().stream().map(com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel::getId).toList(),
                checkInDate,
                checkOutDate,
                numberOfRooms,
                totalDays
        );

        return hotelPage.map(hotel -> mapHotelWithStartingPrice(hotel, startingPrices.get(hotel.getId())));
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

    private Map<Long, BigDecimal> findStartingPricesForHotels(List<Long> hotelIds,
                                                              LocalDate checkInDate,
                                                              LocalDate checkOutDate,
                                                              Integer numberOfRooms,
                                                              Long totalDays) {
        if (hotelIds.isEmpty()) {
            return Map.of();
        }

        Map<HotelRoomKey, List<Inventory>> inventoriesByRoom = inventoryRepository
                .findAvailableInventoriesForHotels(hotelIds, checkInDate, checkOutDate, numberOfRooms)
                .stream()
                .collect(Collectors.groupingBy(inventory -> new HotelRoomKey(
                        inventory.getHotel().getId(),
                        inventory.getRoom().getId()
                )));

        Map<Long, BigDecimal> startingPricesByHotel = new HashMap<>();

        inventoriesByRoom.forEach((hotelRoomKey, inventories) -> {
            if (inventories.size() != totalDays.intValue()) {
                return;
            }

            BigDecimal roomTotal = inventories.stream()
                    .map(pricingService::calculateFinalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(numberOfRooms));

            startingPricesByHotel.merge(
                    hotelRoomKey.hotelId(),
                    roomTotal,
                    BigDecimal::min
            );
        });

        return startingPricesByHotel;
    }

    private HotelResponseDTO mapHotelWithStartingPrice(com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel hotel,
                                                       BigDecimal estimatedStartingPrice) {
        HotelResponseDTO hotelResponseDTO = modelMapper.map(hotel, HotelResponseDTO.class);
        hotelResponseDTO.setEstimatedStartingPrice(estimatedStartingPrice);
        return hotelResponseDTO;
    }

    private record HotelRoomKey(Long hotelId, Long roomId) {
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
