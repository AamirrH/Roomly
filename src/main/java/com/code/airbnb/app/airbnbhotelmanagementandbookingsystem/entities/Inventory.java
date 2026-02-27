package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_inventory_hotel_room_date",
                columnNames = {"hotel_id", "room_id", "date"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many inventory records belong to one hotel.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    /**
     * Many inventory records belong to one room.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "booked_count", nullable = false)
    private Integer bookedCount;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    /**
     * Surge pricing multiplier (e.g. 1.5 means 50% price increase).
     */
    @Column(name = "surge_factor", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal surgeFactor = BigDecimal.ONE;

    /**
     * When true, the room is not available for booking on this date.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean closed = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

