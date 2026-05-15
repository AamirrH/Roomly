package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.LoginDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.LoginResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.Role;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.NoPermissionException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;

    public LoginResponseDTO login(LoginDTO loginDTO) {
        // Authenticate the user first.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        // cast the auth object into an actual user object
        User user = (User) auth.getPrincipal();
        if(user == null){
            throw new UserNotFoundException("User not found");
        }
        if(!(user.getRoles().contains(Role.USER))){
            throw new NoPermissionException("You do not have permission to perform this operation");
        }
        // Generate Tokens
        String accessToken = jwtService.generateJWTAccessToken(user);
        String refreshToken = jwtService.generateJWTRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken);

    }

    public LoginResponseDTO refreshToken(String refreshToken) {
        // First verify the refreshToken
        Long id = jwtService.getUserIDFromJWTToken(refreshToken);
        User user = userService.getUserById(id);
        if(!(user.getRoles().contains(Role.USER))){
            throw new NoPermissionException("You do not have permission to perform this operation");
        }
        // Create a new Access Token
        String newAccessToken = jwtService.generateJWTAccessToken(user);
        return new LoginResponseDTO(newAccessToken,refreshToken);

    }




}
