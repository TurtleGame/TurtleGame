package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurtleOwnerHistoryRepository extends JpaRepository<TurtleOwnerHistory, Integer> {
    List<TurtleOwnerHistory> findByUserAndEndAtIsNull(User user);

    TurtleOwnerHistory findByTurtleIdAndEndAtIsNull(int turtleId);
    TurtleOwnerHistory findByTurtleIdAndUserIdAndEndAtIsNull(int turtleId, int ownerId);
}
