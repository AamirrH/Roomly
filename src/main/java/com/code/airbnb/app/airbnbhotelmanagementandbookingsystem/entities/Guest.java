package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.Gender;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    @ManyToOne
    private User userBookId;

    private String name;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING) // Stores the Enum Values as String.
    private Gender gender;



}
