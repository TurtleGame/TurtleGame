package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleTrainingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurtleTrainingHistoryRepository extends JpaRepository<TurtleTrainingHistory, Integer> {
    List<TurtleTrainingHistory> findByEndAtAfter(LocalDateTime endTime);

    List<TurtleTrainingHistory> findByEndAtBefore(LocalDateTime endTime);

    Boolean existsByTurtleAndEndAtAfter(Turtle turtle, LocalDateTime endTime);

    Boolean existsByTurtleAndEndAtBefore(Turtle turtle, LocalDateTime endTime);

    TurtleTrainingHistory findTopByTurtleIdOrderByEndAtDesc(int turtleId);


}
