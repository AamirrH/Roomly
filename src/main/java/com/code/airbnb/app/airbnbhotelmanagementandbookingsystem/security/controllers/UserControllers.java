package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.controllers;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.LoginDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.LoginResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.JWTService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.LoginService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.header.Header;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roomly/api/v1")
@RequiredArgsConstructor
public class UserControllers {

    private final UserService userService;
    private final LoginService loginService;


    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupDTO signupDTO) {
        return ResponseEntity.ok(userService.signup(signupDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {

       LoginResponseDTO loginResponseDTO = loginService.login(loginDTO);
       Cookie cookie = new Cookie("RefreshToken", loginResponseDTO.getRefreshToken());
       cookie.setHttpOnly(true);
       response.addCookie(cookie);
       return ResponseEntity.ok(loginResponseDTO);

    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh (HttpServletRequest request) {
        String RefreshToken = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("RefreshToken")) {
                RefreshToken = cookie.getValue();
                break;
            }
        }
        LoginResponseDTO loginResponseDTO = loginService.refreshToken(RefreshToken);
        return ResponseEntity.ok(loginResponseDTO);


    }



}
