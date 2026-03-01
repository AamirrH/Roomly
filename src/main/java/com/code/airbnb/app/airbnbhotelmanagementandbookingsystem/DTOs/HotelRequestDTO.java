package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelRequestDTO {

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @NotNull(message = "Contact info is required")
    @Valid                          // triggers nested validation on ContactInfoDto
    private ContactInfoDTO contactInfo;

    @Size(max = 20, message = "Cannot have more than 20 photos")
    @Builder.Default
    private List<String> photos = new ArrayList<>();

    @Size(max = 50, message = "Cannot list more than 50 amenities")
    @Builder.Default
    private List<String> amenities = new ArrayList<>();
}
