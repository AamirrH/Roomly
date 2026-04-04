package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HolidayAPI.HolidayResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HolidayAPIService;
import lombok.RequiredArgsConstructor;

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
        // Call the API here
        List<HolidayResponseDTO> holidayResponseDTOList = holidayAPIService.getHolidays(today.getYear(), "US");
        for(HolidayResponseDTO holidayResponseDTO : holidayResponseDTOList){
            if(holidayResponseDTO.getDate().equals(today)){
                price = price.multiply(BigDecimal.valueOf(1.75));
                // Even if we get multiple holidays we charge only according to one holiday.
                break;
            }
        }
        return price;

    }


}
