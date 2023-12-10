package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleBattleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TurtleBattleHistoryRepository extends JpaRepository<TurtleBattleHistory, Integer> {

}
