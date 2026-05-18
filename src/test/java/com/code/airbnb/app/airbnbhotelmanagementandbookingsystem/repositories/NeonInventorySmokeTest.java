package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("neon")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NeonInventorySmokeTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void findAvailableHotels_returnsExistingHotelFromConfiguredDatabase() {
        LocalDate checkIn = LocalDate.of(2026, 5, 20);
        LocalDate checkOut = LocalDate.of(2026, 5, 23);

        Page<Hotel> hotels = inventoryRepository.findAvailableHotels(
                null,
                checkIn,
                checkOut,
                1,
                3L,
                PageRequest.of(0, 9)
        );

        assertThat(hotels.getContent())
                .as("Expected at least one existing Neon hotel with open inventory from %s to %s", checkIn, checkOut)
                .isNotEmpty();

        hotels.getContent().forEach(hotel ->
                System.out.printf("Neon hotel returned: %s (%s)%n", hotel.getHotelName(), hotel.getCity())
        );
    }
}
