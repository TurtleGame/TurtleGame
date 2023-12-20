package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleEgg;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleEggRepository;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TurtleEggService {

    UserRepository userRepository;
    TurtleEggRepository turtleEggRepository;
    TurtleRepository turtleRepository;
    TurtleStatisticService turtleStatisticService;
    TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;

    @Transactional
    public void warmEgg(int userId, int eggId) throws TurtleNotFoundException, UnauthorizedAccessException {

        TurtleEgg egg = turtleEggRepository.findByIdAndUserId(eggId, userId)
                .orElseThrow(() -> new TurtleNotFoundException("Turtle not found"));

        LocalDateTime hatch = egg.getHatchingAt();
        int warm = egg.getWarming();
        if (warm == 0) throw new IllegalArgumentException("Zółw jest już ogrzany.");
        egg.setWarming(warm - 1);
        egg.setHatchingAt(hatch.minusMinutes(30));

        turtleEggRepository.save(egg);
    }

    @org.springframework.transaction.annotation.Transactional
    public void transformEgg(TurtleEgg egg, User user) {
        LocalDateTime now = LocalDateTime.now();

        Turtle turtle = new Turtle();
        turtle.setAvailable(true);
        turtle.setLevel(0);
        turtle.setName(egg.getName());
        turtle.setTurtleType(egg.getTurtleType());
        turtle.setGender(0);
        turtle.setOwner(user);
        turtle.setEnergy(100);
        turtle.setRankingPoints(0);

        turtle.setFed(false);

        turtleRepository.save(turtle);

        TurtleOwnerHistory history = new TurtleOwnerHistory();
        history.setEndAt(null);
        history.setStartAt(now);
        history.setTurtle(turtle);
        history.setUser(user);
        history.setHowMuch(0);

        turtleOwnerHistoryRepository.save(history);
        turtleStatisticService.addBasicStats(turtle);
        turtleEggRepository.deleteById(egg.getId());
    }
}
