package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.repository;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
