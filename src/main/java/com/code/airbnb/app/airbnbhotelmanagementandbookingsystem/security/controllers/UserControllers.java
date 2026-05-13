package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.controllers;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roomly/api/v1")
@RequiredArgsConstructor
public class UserControllers {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupDTO signupDTO) {
        return ResponseEntity.ok(userService.signup(signupDTO));
    }



}
