package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.UserRoles;
import jakarta.persistence.*;

import java.util.List;

@Entity
// A User may Create Multiple Bookings.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private List<UserRoles> roles;

    private String name;

    private String email;

    private String password;

    @OneToMany(mappedBy = "userId")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "userBookId")
    private List<Guest> guestList;


}
