package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.models.User;
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

    public void abandonTurtle(int turtleId, int ownerId) {

        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(ownerId);
        for (Turtle turtle : user.getTurtles()) {
            if (turtle.getId() == turtleId) {

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
    }

    public Turtle getTurtleDetails(int turtleId, int ownerId) throws TurtleNotFoundException, UnauthorizedAccessException {

        return turtleRepository.findByIdAndOwnerId(turtleId, ownerId)
                .orElseThrow(() -> new TurtleNotFoundException("Turtle not found"));

    }
}
