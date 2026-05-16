package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs.SignupResponseDTO;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.Role;
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

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;


    // Use Email since we are using it as unique identifier to signup, login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException
                ("User with email "+email+" not found."));
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
        if(userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new UserAlreadyExistsException("User already exists.");
        }
            // If the user does not exist, create a new user.
            User user = new User();
            user.setUsername(signupDTO.getUsername());
            user.setEmail(signupDTO.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(signupDTO.getPassword()));
            user.setRoles(Set.of(signupDTO.getRole() == null ? Role.USER : signupDTO.getRole()));
            // Save the user.
            userRepository.save(user);
            return modelMapper.map(user, SignupResponseDTO.class);
    }



}
