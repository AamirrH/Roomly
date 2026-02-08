package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;


    private String city;

    @OneToOne
    private ContactInfo contactInfo;

    @OneToMany(mappedBy = "room")
    private List<Room> rooms;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Boolean isActive;

    @OneToMany(mappedBy = "hotelId")
    private List<Inventory> inventories;







}
