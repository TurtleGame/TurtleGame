package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TurtleService {
    private final TurtleRepository turtleRepository;
    private final TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private final UserRepository userRepository;
    private final UserItemRepository userItemRepository;
    private final TurtleStaticsRepository turtleStaticsRepository;


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

    public void feedTurtle(Integer foodId, Integer userId, Integer turtleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć użytkownika o podanym ID"));


        UserItem item = user.getUserItemList()
                .stream()
                .filter(userItem -> userItem.getItem().getId() == foodId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć przedmiotu użytkownika o podanym ID"));


        item.setQuantity(item.getQuantity() - 1);
        userItemRepository.save(item);

        Turtle turtle = user.getTurtles()
                .stream()
                .filter(turtle1 -> turtle1.getId() == turtleId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć żółwia o podanym ID"));

        turtle.setLevel(turtle.getLevel() + 1);
        turtle.setEnergy(100);
        turtle.setFed(true);

        ItemStatistic itemStatistic = item.getItem().getItemStatistic(item.getItem().getId());
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
