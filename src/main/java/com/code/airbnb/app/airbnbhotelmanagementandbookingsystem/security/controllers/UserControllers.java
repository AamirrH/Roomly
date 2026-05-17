package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.controllers;


import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.LoginDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.LoginResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.UserNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.LoginService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.UserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final LoginService loginService;



    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody @Valid SignupDTO signupDTO) {
        return ResponseEntity.ok(userService.signup(signupDTO));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response) {

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
        if(cookies == null){
            throw new UserNotFoundException("Refresh token not found");
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("RefreshToken")) {
                RefreshToken = cookie.getValue();
                break;
            }
        }
        if(RefreshToken.isBlank()){
            throw new UserNotFoundException("Refresh token not found");
        }
        LoginResponseDTO loginResponseDTO = loginService.refreshToken(RefreshToken);
        return ResponseEntity.ok(loginResponseDTO);


    }



}
