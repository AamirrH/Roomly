package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Test
    void testFindHotelById_whenIdExists_thenReturnHotel()
    {
        Hotel hotel = hotelRepository.findHotelById(18L);

    }

    @Test
    void deleteHotelById() {
    }

    @Test
    void getHotelById() {
    }

    @Test
    void existsById() {
    }

    @Test
    void searchHotelByCity() {
    }

    @Test
    void findAllByCity() {
    }

    @Test
    void getHotelDetails() {
    }
}