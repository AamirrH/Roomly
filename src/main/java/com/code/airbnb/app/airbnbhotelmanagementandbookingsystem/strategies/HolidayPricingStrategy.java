package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;
    // If there's a holiday, increase price
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        LocalDate today = LocalDate.now();
        // TODO : Later
        return price;

    }

    public boolean checkHoliday(LocalDate date){
        // TODO : Later
        return true;

    }


}
