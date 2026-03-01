package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {

    List<Room> findAllRoomsByHotelId(Long hotelId);



}
