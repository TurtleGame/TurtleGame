package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MarketService {
    private TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private TurtleRepository turtleRepository;

    public List<Turtle> getAll(){
        List<TurtleOwnerHistory> history = turtleOwnerHistoryRepository.findAll();
        List<Turtle> turtles = new ArrayList<>();

        for (TurtleOwnerHistory selling : history) {
            if (selling.isSelling()) {
                turtles.add(selling.getTurtle());
            }
        }

        return turtles;
    }

    public int price(Turtle turtle) {
        return 0;
    }

    public void buyTurtle(int turtleId, User oldUser) {
        LocalDateTime now = LocalDateTime.now();
        Turtle turtle = oldUser.getTurtle(turtleId);
        TurtleOwnerHistory turtleOwnerHistory = new TurtleOwnerHistory();

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setEndAt(now);
                    history.setSelling(false);
                    turtleOwnerHistoryRepository.save(history);
                });


        turtle.setOwner(null);

        turtleOwnerHistory.setEndAt(null);
        turtleOwnerHistory.setStartAt(now);
        turtleOwnerHistory.setTurtle(turtle);
        turtleOwnerHistory.setUser(null);
        turtleOwnerHistory.setHowMuch(0);

        turtleOwnerHistoryRepository.save(turtleOwnerHistory);
        turtleRepository.save(turtle);
    }

}
