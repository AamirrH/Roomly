package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.HolidayResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HolidayAPIService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
class AirBnbHotelManagementAndBookingSystemApplicationTests {

    private final HolidayAPIService holidayAPIService;

    static void main() {
        System.out.println(LocalDate.now());
    }
    // Holiday API Test
    @Test
    void testHolidayAPI() {
        List<HolidayResponseDTO> holidays = holidayAPIService.getHolidays(2026, "US");
        System.out.println(holidays);
    }



}
