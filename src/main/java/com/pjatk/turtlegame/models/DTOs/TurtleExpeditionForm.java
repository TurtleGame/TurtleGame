package com.pjatk.turtlegame.models.DTOs;

import com.pjatk.turtlegame.models.Expedition;
import com.pjatk.turtlegame.models.Turtle;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TurtleExpeditionForm {

    private String turtleId;

    private int expeditionId;

    private int durationTime;
}
