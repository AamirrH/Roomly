package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Booking;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.entities.Guest;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.utlis.PermissionMapping;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;



     // Stores the list of roles as a collection of enum strings in a separate join table.
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    // Bidirectional: one user can have many bookings
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    // Bidirectional: one user can have many guests
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Guest> guests;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (this.roles == null) {
            return authorities;
        }
        this.roles.forEach(role -> {
            Set<SimpleGrantedAuthority> roleAuthorities = PermissionMapping
                    .getAuthoritiesForRole(role);
            authorities.addAll(roleAuthorities);
        });
        return authorities;
    }

    // Username Method returns email because email has been used as a primary identifier in authentication.
    @Override
    public String getUsername() {
        return this.email;
    }

    public String getDisplayName(){
        return this.username;
    }
    public void setDisplayName(String username){
        this.username = username;
    }

}
