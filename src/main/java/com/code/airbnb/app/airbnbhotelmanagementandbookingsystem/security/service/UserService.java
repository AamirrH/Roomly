package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.UserAlreadyExistsException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.exceptions.UserNotFoundException;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
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

    public SignupResponseDTO signup(SignupDTO signupDTO) throws UserAlreadyExistsException {
        // We use email since it is unique.
        if(userRepository.findByUsername(signupDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with Username "+signupDTO.getUsername()+" already exists.");
        }
            // If the user does not exist, create a new user.
            User user = new User();
            user.setUsername(signupDTO.getUsername());
            user.setEmail(signupDTO.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(signupDTO.getPassword()));
            // Save the user.
            userRepository.save(user);
            return modelMapper.map(signupDTO, SignupResponseDTO.class);
    }



}
