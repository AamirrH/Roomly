package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.configuration;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final String [] publicRoutes = {"/roomly/api/v1/login","/roomly/api/v1/logout","/roomly/api/v1/signup"};


    @Bean
    // TODO : Request Matchers will be added at the last
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers(publicRoutes).permitAll()
                                .anyRequest()
                                .authenticated())
                .csrf(csrfConfigurer ->  csrfConfigurer.disable())
                .formLogin(formLoginConfigurer -> formLoginConfigurer.disable());






        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




}
