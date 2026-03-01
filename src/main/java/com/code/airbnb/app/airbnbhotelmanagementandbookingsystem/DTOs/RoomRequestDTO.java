package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequestDTO {

    @NotBlank(message = "Room type is required")
    @Size(min = 2, max = 50, message = "Room type must be between 2 and 50 characters")
    private String type;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Base price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal basePrice;

    @NotNull(message = "Total count is required")
    @Min(value = 1, message = "Total count must be at least 1")
    @Max(value = 1000, message = "Total count cannot exceed 1000")
    private Integer totalCount;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 20, message = "Capacity cannot exceed 20")
    private Integer capacity;

    @Size(max = 30, message = "Cannot list more than 30 amenities")
    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    @Size(max = 20, message = "Cannot have more than 20 photos")
    @Builder.Default
    private List<String> photos = new ArrayList<>();

}
