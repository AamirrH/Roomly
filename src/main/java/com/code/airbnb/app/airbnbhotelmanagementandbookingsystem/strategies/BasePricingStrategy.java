package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class BasePricingStrategy implements  PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }


}
