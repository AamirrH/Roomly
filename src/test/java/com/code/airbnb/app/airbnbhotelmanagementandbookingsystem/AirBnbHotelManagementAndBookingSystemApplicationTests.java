package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.CalenderItemsDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HolidayAPIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AirBnbHotelManagementAndBookingSystemApplicationTests {

    @Autowired
    private HolidayAPIService holidayAPIService;

    // Holiday API Test
    @Test
    void testHolidayAPI() {
        List<CalenderItemsDTO> holidays = holidayAPIService.getHolidays(
                "2026-01-01T00:00:00Z",
                "2026-12-31T23:59:59Z"
        );

        assertNotNull(holidays);
        assertFalse(holidays.isEmpty());

        for (CalenderItemsDTO holiday : holidays) {
            assertNotNull(holiday.getSummary());
            assertNotNull(holiday.getStart());
            assertNotNull(holiday.getStart().getDate());

            System.out.println(holiday.getStart().getDate() + " - " + holiday.getSummary());
        }
    }




    // Pricing Strategy Test
    @Test
    void testPricingStrategy(){


    }





}
