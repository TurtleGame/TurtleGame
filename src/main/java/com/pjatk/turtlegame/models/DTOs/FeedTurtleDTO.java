package com.pjatk.turtlegame.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeedTurtleDTO {

    private Integer turtleId;
    private Integer foodId;
}
