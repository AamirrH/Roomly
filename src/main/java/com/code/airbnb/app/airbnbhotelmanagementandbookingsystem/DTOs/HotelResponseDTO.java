package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;


import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class HotelResponseDTO {

    private Long id;

    private String city;

    private List<String> photos = new ArrayList<>();

    private List<String> amenities = new ArrayList<>();

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
