package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TurtleService {
    private final TurtleRepository turtleRepository;
    private final TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final TurtleStaticsRepository turtleStaticsRepository;
    private final ItemStatisticRepository itemStatisticRepository;
    private final GuardsRepository guardsRepository;


    public void abandonTurtle(int turtleId, User user) {
        LocalDateTime now = LocalDateTime.now();
        Turtle turtle = user.getTurtle(turtleId);

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setEndAt(now);
                    turtleOwnerHistoryRepository.save(history);
                });
        turtle.setOwner(null);
        turtleRepository.save(turtle);

    }

    public Turtle getTurtleDetails(int turtleId, int ownerId) throws TurtleNotFoundException, UnauthorizedAccessException {

        return turtleRepository.findByIdAndOwnerId(turtleId, ownerId)
                .orElseThrow(() -> new TurtleNotFoundException("Turtle not found"));

    }

    @Transactional
    public void feedTurtle(Integer foodId, User user, Integer turtleId) throws TurtleNotFoundException {
        Turtle turtle = user.getTurtle(turtleId);
        if (turtle == null) {
            throw new TurtleNotFoundException("Turtle not found.");
        }
        if (turtle.isFed()) {
            throw new IllegalArgumentException("Zółw jest już nakarmiony.");
        }

        itemService.removeItem(user, foodId, 1);

        turtle.setLevel(turtle.getLevel() + 1);
        turtle.setEnergy(100);
        turtle.setFed(true);
        turtle.setHowMuchFood(turtle.getHowMuchFood() + 1);

        List<ItemStatistic> itemStatistics = itemStatisticRepository.findAllByItemId(foodId);
        for (ItemStatistic itemStatistic : itemStatistics) {
            Optional<TurtleStatistic> statisticToImprove = turtle.getTurtleStatisticList()
                    .stream()
                    .filter(stat -> stat.getStatistic().getId() == itemStatistic.getStatistic().getId())
                    .findFirst();

            if (statisticToImprove.isPresent()) {
                statisticToImprove.get().setValue(statisticToImprove.get().getValue() + itemStatistic.getValue());
                turtleStaticsRepository.save(statisticToImprove.get());
            }
        }
    }

    public void sellTurtle(int userId, int turtleId, int shells) {
        User user = userRepository.findById(userId);
        Turtle turtle = user.getTurtle(turtleId);

        for (TurtleOwnerHistory selling : turtle.getTurtleOwnerHistoryList()) {
            if (selling.getEndAt() == null) {
                selling.setSelling(true);
                selling.setHowMuch(shells);
                turtleOwnerHistoryRepository.save(selling);
            }
        }
        turtle.setOwner(null);
        turtleRepository.save(turtle);
    }

    public void fightWithGuards(int turtleId, int guardsId) {
        Turtle turtle = turtleRepository.findById(turtleId).orElseThrow();
        Guard guard = guardsRepository.findById(guardsId).orElseThrow();
        int turtleHP = turtle.getTurtleStatisticList().stream().filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 1).map(TurtleStatistic::getValue).findFirst().orElseThrow();
        int guardHP = guard.getGuardStatistics().stream().filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 1).map(GuardStatistic::getValue).findFirst().orElseThrow();

        int turtleAgility = turtle.getTurtleStatisticList().stream().filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 3).map(TurtleStatistic::getValue).findFirst().orElseThrow();
        int guardAgility = guard.getGuardStatistics().stream().filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 3).map(GuardStatistic::getValue).findFirst().orElseThrow();

        while (turtleHP > 0 && guardHP > 0) {
            if (turtleAgility > guardAgility) {
                    
            }
        }

    }


}
