package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleExpeditionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurtleExpeditionHistoryRepository extends JpaRepository<TurtleExpeditionHistory, Integer> {
}
