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
public class BuyTurtle {
    @NotNull
    private Integer turtleId;

    @NotNull(message = "Brak wybranego pokarmu")
    private Integer foodId;
}
