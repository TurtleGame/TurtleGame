package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MarketService {
    private TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private TurtleRepository turtleRepository;
    private UserRepository userRepository;

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

    public int sellerIsBuyer (User user, Turtle turtle){
        List<TurtleOwnerHistory> history = turtleOwnerHistoryRepository.findAll();
        int userId = 0;

        for (TurtleOwnerHistory hist : history) {
            if (turtle.getId() == hist.getTurtle().getId()) {
                if (hist.getEndAt() == null)
                    userId = hist.getUser().getId();
            }
        }

        if (user.getId() == userId)
            return 1;

        return 0;
    }

    public int price(Turtle turtle) {
        int gold = 0;

        for (TurtleOwnerHistory selling : turtle.getTurtleOwnerHistoryList()) {
            if (selling.getEndAt() == null) {
                gold = selling.getHowMuch();
            }
        }

        return gold;
    }

    public void buyTurtle(int turtleId, User newUser) {
        LocalDateTime now = LocalDateTime.now();
        User oldUser = turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtleId).getUser();
        TurtleOwnerHistory transaction = turtleOwnerHistoryRepository.findByTurtleIdAndUserIdAndEndAtIsNull(turtleId, oldUser.getId());
        Turtle turtle = transaction.getTurtle();
        TurtleOwnerHistory turtleOwnerHistory = new TurtleOwnerHistory();

        if (newUser.getGold() > price(turtle)) {

            newUser.setGold(newUser.getGold() - price(turtle));
            userRepository.save(newUser);

        } else if (newUser.getGold() == price(turtle)) {

            newUser.setGold(0);
            userRepository.save(newUser);

        } else {

            throw new IllegalArgumentException("Brak wystarczającej ilości");

        }

        oldUser.setGold(oldUser.getGold() + price(turtle));

        userRepository.save(oldUser);

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setEndAt(now);
                    history.setSelling(false);
                    turtleOwnerHistoryRepository.save(history);
                });



        turtle.setOwner(newUser);

        turtleOwnerHistory.setEndAt(null);
        turtleOwnerHistory.setStartAt(now);
        turtleOwnerHistory.setTurtle(turtle);
        turtleOwnerHistory.setUser(newUser);
        turtleOwnerHistory.setHowMuch(price(turtle));

        turtleOwnerHistoryRepository.save(turtleOwnerHistory);
        turtleRepository.save(turtle);
    }

    public void undoTurtle(int turtleId, User user) {
        Turtle turtle = turtleOwnerHistoryRepository.findByTurtleIdAndUserIdAndEndAtIsNull(turtleId, user.getId()).getTurtle();

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setSelling(false);
                    turtleOwnerHistoryRepository.save(history);
                });

        turtle.setOwner(user);
        turtleRepository.save(turtle);
    }
}
