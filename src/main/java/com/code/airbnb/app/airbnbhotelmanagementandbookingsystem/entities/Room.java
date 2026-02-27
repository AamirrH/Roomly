package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many rooms belong to one hotel.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private String type;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer capacity;

    @ElementCollection
    @CollectionTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id")
    )
    @Column(name = "amenity")
    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "room_photos",
            joinColumns = @JoinColumn(name = "room_id")
    )
    @Column(name = "photo_url", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> photos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One room can have many bookings
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // One room can have many inventory records
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Inventory> inventories = new ArrayList<>();
}
