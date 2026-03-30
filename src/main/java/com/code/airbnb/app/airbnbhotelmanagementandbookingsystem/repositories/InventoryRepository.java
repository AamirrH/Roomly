package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Inventory;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {


    void deleteByRoom_IdAndHotel_Id(Long roomId, Long hotelId);

    @Query(value = """
       SELECT DISTINCT i.hotel FROM Inventory i
       WHERE i.hotel.city = :city
       AND i.date >= :checkInDate AND i.date < :checkOutDate
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


    @Query("""
   SELECT DISTINCT i.room FROM Inventory i
   WHERE i.hotel.id = :hotelId
   AND i.date >= :checkInDate AND i.date < :checkOutDate
   AND i.closed = false
   AND (i.totalCount - i.bookedCount) >= :numberOfRooms
   GROUP BY i.room
   HAVING COUNT(i) = :totalDays
   """)
    List<Room> findAvailableRoomsForHotel(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfRooms") Integer numberOfRooms,
            @Param("totalDays") Long totalDays
    );

    @Query(value =
            """
    SELECT i from  Inventory i
        WHERE i.room.id = :roomId
            AND i.hotel.id = :hotelId
             AND i.date >= :checkInDate AND i.date < :checkOutDate
              AND i.closed = false
               AND (i.totalCount - i.bookedCount) >= :numberOfRooms
    """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    /*For the specific rows that result due to that query, PESSIMISTIC_WRITE,
    Other transactions that try to access the same row wait until your transaction finishes.
     */
    List<Inventory> findAvailableInventoriesAndLockThem(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfRooms") Integer numberOfRooms
    );

    @Query(value =
            """
    SELECT i from  Inventory i
        WHERE i.room.id = :roomId
            AND i.hotel.id = :hotelId
             AND i.date >= :checkInDate AND i.date < :checkOutDate
    """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findBookedInventoriesAndLockThem(
            @Param("hotelId") Long hotelId,
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );


}
