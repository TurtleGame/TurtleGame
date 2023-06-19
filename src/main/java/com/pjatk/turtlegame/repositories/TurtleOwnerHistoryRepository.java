package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TurtleOwnerHistoryRepository extends JpaRepository<TurtleOwnerHistory, Integer> {
    List<TurtleOwnerHistory> findByUserAndEndAtIsNull(User user);
}
