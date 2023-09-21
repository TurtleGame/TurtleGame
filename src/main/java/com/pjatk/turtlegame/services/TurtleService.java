package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TurtleService {
    private final TurtleRepository turtleRepository;
    private final TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final TurtleStaticsRepository turtleStaticsRepository;
    private final ItemStatisticRepository itemStatisticRepository;


    public void abandonTurtle(int turtleId, int ownerId) {

        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(ownerId);

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
    public void feedTurtle(Integer foodId, Integer userId, Integer turtleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć użytkownika o podanym ID"));
        Turtle turtle = user.getTurtle(turtleId);

        if (turtle.isFed()) {
            throw new IllegalArgumentException("Zółw jest już nakarmiony.");
        }

        itemService.removeItem(userId, foodId, 1);

        turtle.setLevel(turtle.getLevel() + 1);
        turtle.setEnergy(100);
        turtle.setFed(true);

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
}
