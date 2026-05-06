package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final HolidayAPIService holidayAPIService;

    public BigDecimal calculateFinalPrice(Inventory inventory){
        PricingStrategy pricingStrategy = new BasePricingStrategy();
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupanyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(holidayAPIService,pricingStrategy);
         return pricingStrategy.calculatePrice(inventory);

    }


}
