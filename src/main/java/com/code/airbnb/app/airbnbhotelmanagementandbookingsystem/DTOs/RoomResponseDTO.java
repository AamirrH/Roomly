package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseDTO {

    private Long id;

    private Long hotelId;
    // just the ID, not the full hotel to avoid deep nesting
    private String type;

    private BigDecimal basePrice;

    private Integer totalCount;

    private Integer capacity;

    private List<String> amenities = new ArrayList<>();

    private List<String> photos = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
