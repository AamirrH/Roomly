package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.DTOs;


import jakarta.validation.constraints.*;
import jakarta.websocket.OnMessage;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 6,max = 20,message = "Username must be minimum 6 characters")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*" ,message = "Username can only contain alphanumeric characters and " +
            "underscore and must not start with underscore or digits")
    private String username;
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 20,message = "Password must be minimum 10 characters long")
    @Pattern(
            regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,64}",
            message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    private String password;


}
