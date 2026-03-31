package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


// Raise Price if currentDate is 2-3 days before checkInDate
@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        // Get Current Date
        LocalDate today = LocalDate.now();


    }
}
