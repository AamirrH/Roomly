package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.repositories;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
