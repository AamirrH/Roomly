package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.BookingResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Booking;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {


    List<BookingResponseDTO> findByUser_Id(Long userId);

    @Query(
                    """
                    SELECT b from Booking b
                    WHERE b.hotel.id = :hotelId
                    AND (:startDate IS NULL OR b.checkInDate >= :startDate)
                    AND (:endDate IS NULL OR b.checkOutDate <= :endDate)
                    AND (:finalStatus IS NULL OR b.status = :finalStatus)
                   \s"""
    )
    List<Booking> getBookingsForHotelManager(@Param("hotelId") Long hotelId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("finalStatus") BookingStatus finalStatus);

    @Query(
            """
            SELECT b FROM Booking b
            WHERE (:startDate IS NULL OR b.checkInDate >= :startDate)
            AND (:endDate IS NULL OR b.checkOutDate <= :endDate)
            """
    )
    List<Booking> findBookingsForReport(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}
