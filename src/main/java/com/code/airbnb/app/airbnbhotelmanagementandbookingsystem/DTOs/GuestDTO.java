package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.DTOs;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.enums.Gender;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Name must not be blank")
    private String email;
    @NotBlank(message = "Name must not be blank")
    private String phoneNumber;
    @Enumerated
    private Gender gender;

}
