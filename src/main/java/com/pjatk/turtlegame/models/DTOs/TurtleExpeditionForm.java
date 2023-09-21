package com.pjatk.turtlegame.models.DTOs;

import com.pjatk.turtlegame.models.Expedition;
import com.pjatk.turtlegame.models.Turtle;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TurtleExpeditionForm {

    @NotNull
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

    @NotNull
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;

    @NotNull
    private int durationTime;
}
