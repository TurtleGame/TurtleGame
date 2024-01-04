package com.pjatk.turtlegame.models.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class UserDTO {

    @NotNull(message = "Email nie może być pusty!")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Niepoprawny format adresu email")
    private String email;

    @Size(max = 28, message = "Nick za długi!")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Nick musi się składać z samych liter i cyfr")
    private String username;

    @Size(min = 6, message = "Hasło musi miec minimum 6 znaków")
    private String password;


}
