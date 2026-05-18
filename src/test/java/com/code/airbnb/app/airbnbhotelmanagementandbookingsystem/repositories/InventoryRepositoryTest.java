package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void findAvailableHotels_whenHotelHasMultipleAvailableRooms_returnsHotelOnce() {
        Hotel hotel = Hotel.builder()
                .hotelName("Roomly Test Resort")
                .city("Mumbai")
                .active(true)
                .photos(new ArrayList<>())
                .amenities(new ArrayList<>(List.of("Wifi", "Pool")))
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);

        Room deluxeRoom = createRoom(savedHotel, "Deluxe", "4500.00", 8, 2);
        Room suiteRoom = createRoom(savedHotel, "Suite", "7500.00", 4, 4);
        List<Room> savedRooms = roomRepository.saveAll(List.of(deluxeRoom, suiteRoom));

        LocalDate checkIn = LocalDate.of(2026, 5, 20);
        LocalDate checkOut = LocalDate.of(2026, 5, 23);
        List<Inventory> inventories = new ArrayList<>();

        for (Room room : savedRooms) {
            LocalDate date = checkIn;
            while (date.isBefore(checkOut)) {
                inventories.add(Inventory.builder()
                        .hotel(savedHotel)
                        .room(room)
                        .date(date)
                        .bookedCount(0)
                        .totalCount(room.getTotalCount())
                        .surgeFactor(BigDecimal.ONE)
                        .closed(false)
                        .build());
                date = date.plusDays(1);
            }
        }
        inventoryRepository.saveAll(inventories);

        Page<Hotel> result = inventoryRepository.findAvailableHotels(
                null,
                checkIn,
                checkOut,
                1,
                3L,
                PageRequest.of(0, 9)
        );

        assertThat(result.getContent())
                .extracting(Hotel::getHotelName)
                .containsExactly("Roomly Test Resort");
    }

    private Room createRoom(Hotel hotel, String type, String basePrice, Integer totalCount, Integer capacity) {
        return Room.builder()
                .hotel(hotel)
                .type(type)
                .basePrice(new BigDecimal(basePrice))
                .totalCount(totalCount)
                .capacity(capacity)
                .photos(new ArrayList<>())
                .amenities(new ArrayList<>(List.of("Wifi")))
                .build();
    }
}
