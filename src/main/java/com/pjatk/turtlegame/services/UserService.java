package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;

    public List<Turtle> getTurtles(User user) {
        List<TurtleOwnerHistory> turtleOwnerHistoryList = turtleOwnerHistoryRepository.findByUserAndEndAtIsNull(user);
        List<Turtle> turtles = new ArrayList<>();
        for (TurtleOwnerHistory item : turtleOwnerHistoryList) {
            turtles.add(item.getTurtle());
        }

        return turtles;
    }


}
