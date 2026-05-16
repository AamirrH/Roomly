package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JWTService {

    @Value("${JWT_SECRET_KEY}")
    private String jwtsecretKey;

    // Encoding the secret key
    private SecretKey HMACGeneratedKey(){
        return Keys.hmacShaKeyFor(jwtsecretKey.getBytes(StandardCharsets.UTF_8));
    }


    // Generating a JWT Access Token
    public String generateJWTAccessToken(User user){
        // builder -> builds the token , parser -> parses/breaks the token
        return Jwts.builder()
                .subject(user.getId().toString())
                /*Claims are basically the payload of the JWT token which contains
                 non-sensitive data */
                .claim("email", user.getEmail())
                .claim("username", user.getDisplayName())
                .claim("roles",user.getRoles().toString())
                // IAT -> the time in milliseconds when the token was issued
                .issuedAt(new Date(System.currentTimeMillis()))
                // expiration -> the time after which token will get expired i.e. after 10 minutes for seamless experience
                .expiration(new Date(System.currentTimeMillis()+60*1000*10))
                // Sign it with the encoded signature using a secret Key
                .signWith(HMACGeneratedKey())
                .compact();

    }

    // Generating a Refresh Token
    public String generateJWTRefreshToken(User user){
        // builder -> builds the token , parser -> parses/breaks the token
        return Jwts.builder()
                .subject(user.getId().toString())
                /*Claims are basically the payload of the JWT token which contains
                 non-sensitive data */
                .claim("email", user.getEmail())
                .claim("username", user.getDisplayName())
                .claim("roles",user.getRoles())
                // IAT -> the time in milliseconds when the token was issued
                .issuedAt(new Date(System.currentTimeMillis()))
                // expiration -> the time after which token will get expired, after 30 minutes
                .expiration(new Date(System.currentTimeMillis()+1000*30*60))
                // Sign it with the encoded signature using a secret Key
                .signWith(HMACGeneratedKey())
                .compact();

    }

    // Parsing a JWT Token
    // Extracts only the "Subject" from the JWT
    public Long getUserIDFromJWTToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(HMACGeneratedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());

    }


}

