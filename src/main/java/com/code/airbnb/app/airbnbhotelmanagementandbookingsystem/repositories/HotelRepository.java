package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.HotelResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs.RoomResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    Hotel findHotelById(Long id);

    void deleteHotelById(Long id);

    Hotel getHotelById(Long id);


    boolean existsById(Long id);

    HotelResponseDTO searchHotelByCity(String city);

    List<Hotel> findAllByCity(String city);

    @Query(
       """
       SELECT rooms FROM Hotel
              WHERE id = :hotelId

      """
    )
    Page<RoomResponseDTO> getHotelDetails(@Param("hotelId") Long hotelId,
                                          Pageable pageable);

}
