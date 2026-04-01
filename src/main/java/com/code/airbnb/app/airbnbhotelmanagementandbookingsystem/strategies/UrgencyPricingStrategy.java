package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.strategies;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.exceptions.BookingExpiredException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


// Raise Price if currentDate is 2-3 days before checkInDate
@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy{

    private static final int urgencyWindow = 4;
    private static final double urgencyRate4Days = 1.25;
    private static final double urgencyRateLessThan4Days = 1.5;
    private static final double urgencyRate0Days = 2;
    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        // Get Current Date
        LocalDate today = LocalDate.now();
        // Urgency -> 4 days before booking
        long daysBetween = ChronoUnit.DAYS.between(today, inventory.getDate());

        if(daysBetween == 0){
            price = price.multiply(BigDecimal.valueOf(urgencyRate0Days));
        }
        else if(daysBetween < urgencyWindow){
            price = price.multiply(BigDecimal.valueOf(urgencyRateLessThan4Days));

        }
        else if(daysBetween == urgencyWindow){
            price = price.multiply(BigDecimal.valueOf(urgencyRate4Days));
        }
        return price;

    }
}
