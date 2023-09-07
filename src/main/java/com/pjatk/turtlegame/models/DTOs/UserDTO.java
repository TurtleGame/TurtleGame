package com.pjatk.turtlegame.models.DTOs;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Column(unique = true)
    private String username;

    private String password;


}
