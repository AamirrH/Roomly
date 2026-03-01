package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInfoDTO {

    @NotBlank(message = "Address is required")
    private String completeAddress;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be 7-15 digits, optionally starting with +"
    )
    private String phoneNumber;

}
