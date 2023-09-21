package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurtleStaticsRepository extends JpaRepository<TurtleStatistic, Integer> {

}
