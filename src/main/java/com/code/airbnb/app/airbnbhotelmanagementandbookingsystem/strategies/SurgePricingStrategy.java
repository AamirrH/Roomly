package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

// Uses Surge Factor to increase the price

public class SurgePricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    public SurgePricingStrategy(PricingStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal surgeFactor = inventory.getSurgeFactor();
        BigDecimal price = wrapped.calculatePrice(inventory);
        return price.multiply(BigDecimal.valueOf(surgeFactor.doubleValue()));
    }
}
