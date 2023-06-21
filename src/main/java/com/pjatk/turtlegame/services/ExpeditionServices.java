package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Expedition;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleExpeditionHistory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import org.springframework.validation.BindingResult;


import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ExpeditionServices {

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
