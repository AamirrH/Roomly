package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    private String email;
    private String password;


}
