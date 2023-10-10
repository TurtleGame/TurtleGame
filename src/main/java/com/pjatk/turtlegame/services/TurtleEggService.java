package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.TurtleEgg;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleEggRepository;
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
}
