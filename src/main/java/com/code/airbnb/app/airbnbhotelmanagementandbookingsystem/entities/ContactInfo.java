package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import jakarta.persistence.*;

@Entity
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    private String completeAddress;

    private String location;

    private String email;

    private String phoneNumber;

    // To Keep consistency of Objects
    @OneToOne
     private Hotel hotel;

}
