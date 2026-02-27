package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    /**
     * One hotel has exactly one ContactInfo.
     * Hotel owns the FK column contact_info_id.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_info_id", referencedColumnName = "id")
    private ContactInfo contactInfo;

    /**
     * Stores photo URLs as a simple element collection in a separate table.
     */
    @ElementCollection
    @CollectionTable(
            name = "hotel_photos",
            joinColumns = @JoinColumn(name = "hotel_id")
    )
    @Column(name = "photo_url", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> photos = new ArrayList<>();

    /**
     * Stores amenity strings as a simple element collection.
     */
    @ElementCollection
    @CollectionTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id")
    )
    @Column(name = "amenity")
    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One hotel has many rooms
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    // One hotel has many bookings
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // One hotel has many inventory records
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Inventory> inventories = new ArrayList<>();
}
