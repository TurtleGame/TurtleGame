package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TurtleService {
    TurtleRepository turtleRepository;
    TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    UserRepository userRepository;

    public void abandonTurtle(int id) throws Exception {
        Turtle turtle = turtleRepository.findById(id)
                .orElseThrow(() -> new Exception("Turtle not found"));

        LocalDateTime now = LocalDateTime.now();

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setEndAt(now);
                    turtleOwnerHistoryRepository.save(history);
                });

        turtle.setOwner(null);
        turtleRepository.save(turtle);
    }


}
