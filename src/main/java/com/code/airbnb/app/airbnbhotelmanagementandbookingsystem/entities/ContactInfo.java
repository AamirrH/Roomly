package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complete_address")
    private String completeAddress;

    private String location;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Bidirectional one-to-one back-reference to Hotel.
     * Hotel owns the FK; this side is the inverse (mappedBy).
     */
    @OneToOne(mappedBy = "contactInfo")
    private Hotel hotel;
}
