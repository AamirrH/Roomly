package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {


    void deleteByRoom_IdAndHotel_Id(Long roomId, Long hotelId);

    @Query("""
       SELECT DISTINCT i.hotel FROM Inventory i
       WHERE i.hotel.city = :city
       AND i.date BETWEEN :checkInDate AND :checkOutDate
       AND i.closed = false
       AND (i.totalCount - i.bookedCount) >= :numberOfRooms
       GROUP BY i.hotel
       HAVING COUNT(i) = :totalDays
       """)
    Page<Hotel> findAvailableHotels(
            @Param("city") String city,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfRooms") Integer numberOfRooms,
            @Param("totalDays") Long totalDays,
            Pageable pageable

    );
}
