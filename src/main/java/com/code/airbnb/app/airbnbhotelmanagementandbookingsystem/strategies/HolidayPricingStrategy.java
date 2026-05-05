package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.CalenderItemsDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.HolidayResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HolidayAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private final HolidayAPIService holidayAPIService;
    private final PricingStrategy wrapped;
    // If there's a holiday, increase price
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        LocalDate today = LocalDate.now();

        String currentYear = String.valueOf(today.getYear());

        String timeMin = currentYear + "-01-01T00:00:00Z";

        String timeMax = currentYear + "-12-31T23:59:59Z";

        // Call the API here and get all the holidays of the current year
        List<CalenderItemsDTO> holidayResponses = holidayAPIService.getHolidays(timeMin,timeMax);
        for( CalenderItemsDTO holidayResponseDTO : holidayResponses){
            if(holidayResponseDTO.getStart().getDate().equals(today)){
                price = price.multiply(BigDecimal.valueOf(1.75));
                // Even if we get multiple holidays we charge only according to one holiday.
                break;
            }
        }
        return price;

    }


}
