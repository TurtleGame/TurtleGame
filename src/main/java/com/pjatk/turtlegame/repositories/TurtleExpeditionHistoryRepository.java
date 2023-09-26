package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurtleExpeditionHistoryRepository extends JpaRepository<TurtleExpeditionHistory, Integer> {
    List<TurtleExpeditionHistory> findByEndAtAfter(LocalDateTime endTime);

    List<TurtleExpeditionHistory> findByEndAtBefore(LocalDateTime endTime);

    Boolean existsByTurtleAndEndAtAfter(Turtle turtle, LocalDateTime endTime);

    Boolean existsByTurtleAndEndAtBefore(Turtle turtle, LocalDateTime endTime);

    TurtleExpeditionHistory findTopByTurtleIdOrderByEndAtDesc(int turtleId);


}
