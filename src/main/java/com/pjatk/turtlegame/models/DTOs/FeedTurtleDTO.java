package com.pjatk.turtlegame.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FeedTurtleDTO {

    private int turtleId;
    private int foodId;
}
