package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleBattleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TurtleBattleHistoryRepository extends JpaRepository<TurtleBattleHistory, Integer> {

    List<TurtleBattleHistory> findTurtleBattleHistoriesByWinnerTurtleOrLoserTurtleOrderByCreatedAtDesc(Turtle winnerTurtle, Turtle loserTurtle);


}
