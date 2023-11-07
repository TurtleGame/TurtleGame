package com.pjatk.turtlegame.models.DTOs;

import com.pjatk.turtlegame.models.Training;
import com.pjatk.turtlegame.models.Turtle;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TurtleTrainingForm {

    @NotNull(message = "Musisz wybrać żółwia!")
    private Turtle turtle;

    @NotNull(message = "Musisz wybrać trening!")
    private Training training;

    @NotNull(message = "Musisz wybrać długość treningu!")
    private int durationTime;
}
