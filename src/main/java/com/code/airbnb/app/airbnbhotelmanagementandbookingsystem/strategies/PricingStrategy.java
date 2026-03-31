package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
