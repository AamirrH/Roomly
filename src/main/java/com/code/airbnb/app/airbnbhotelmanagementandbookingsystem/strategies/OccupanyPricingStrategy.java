package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

// If Occupancy is above a certain level, increase prices bookedCount/totalCount
@RequiredArgsConstructor
public class OccupanyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price =  wrapped.calculatePrice(inventory);
        Integer bookedCount = inventory.getBookedCount();
        Integer totalCount = inventory.getTotalCount();
        double occupancy = (double) bookedCount/totalCount;
        // Logic
        if(occupancy>0.8){
            price = price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
