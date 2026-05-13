package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.UserNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException
                ("User with Username "+username+" not found."));
        return user;

    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with Email "+email+" not found."));

    }

    public User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id "+id+" not found."));
    }



}
