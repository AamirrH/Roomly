package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
