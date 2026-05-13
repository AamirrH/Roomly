package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class SignupResponseDTO {

    private String username;
    private String email;


}
