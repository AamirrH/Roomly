package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;


}
