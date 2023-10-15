package com.pjatk.turtlegame.models.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EggsForm {
    @NotNull
    private Integer eggId;
    @NotNull
    @Size(min = 2, max = 50, message = "Imię żółwia musi mieć od 2 do 50 znaków!")
    private String name;
}
