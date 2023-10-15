package com.pjatk.turtlegame.models.DTOs;

import com.pjatk.turtlegame.models.Expedition;
import com.pjatk.turtlegame.models.Turtle;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TurtleExpeditionForm {

    @NotNull(message = "Musisz wybrać żółwia!")
    private Turtle turtle;

    @NotNull(message = "Musisz wybrać wyprawę!")
    private Expedition expedition;

    @NotNull(message = "Musisz wybrać długość wyprawy!")
    private int durationTime;
}
