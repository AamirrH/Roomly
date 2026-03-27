package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.services;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public void initialiseRoomForAYear(Room room){
        LocalDate dateToday = LocalDate.now();
        LocalDate dateEnd = dateToday.plusYears(1);

        List<Inventory> inventoryList = new ArrayList<>();

        while(!dateToday.isAfter(dateEnd)) {
            Inventory inventory = new Inventory();
            inventory.setRoom(room);
            inventory.setHotel(room.getHotel());
            inventory.setBookedCount(0);
            inventory.setTotalCount(room.getTotalCount());
            inventory.setSurgeFactor(BigDecimal.valueOf(1));
            inventory.setDate(dateToday);
            inventoryList.add(inventory);
            dateToday = dateToday.plusDays(1);
        }

        // Single-DB call is efficient
        inventoryRepository.saveAll(inventoryList);
    }
    public void deleteByRoomIdAndHotelId(Long roomId, Long hotelId) {
        // Deletes all rows with the specified roomId and HotelId
        inventoryRepository.deleteByRoom_IdAndHotel_Id(roomId, hotelId);
    }


}
