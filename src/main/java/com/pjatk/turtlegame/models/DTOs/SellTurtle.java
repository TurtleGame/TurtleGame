package com.pjatk.turtlegame.models.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellTurtle {
    @NotNull
    private Integer turtleId;

    @NotNull(message = "Musisz ustalić cenę!")
    private int gold;

    @NotNull(message = "Musisz wybrać ilość!")
    private int quantity;
}
