package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Expedition;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleExpeditionHistory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;


import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ExpeditionService {

    TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;


    public TurtleExpeditionHistory turtleExpedition(Turtle turtle, Expedition expedition, int durationTime) {


        TurtleExpeditionHistory turtleExpedition = new TurtleExpeditionHistory();
        turtleExpedition.setTurtle(turtle);
        turtleExpedition.setExpedition(expedition);
        turtleExpedition.setStartAt(LocalDateTime.now());
        turtleExpedition.setEndAt(turtleExpedition.getStartAt().plusMinutes(durationTime));
        turtle.setAvailable(false);

        return turtleExpedition;
    }
}
