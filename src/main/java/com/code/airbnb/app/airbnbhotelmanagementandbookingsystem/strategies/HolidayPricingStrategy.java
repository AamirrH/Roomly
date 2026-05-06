package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services.HolidayAPIService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private final HolidayAPIService holidayAPIService;
    private final PricingStrategy wrapped;
    // If there's a holiday, increase price
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        LocalDate inventoryDate = inventory.getDate();

        if (holidayAPIService.isPublicHoliday(inventoryDate)) {
            price = price.multiply(BigDecimal.valueOf(1.75));
        }
        return price;

    }


}
