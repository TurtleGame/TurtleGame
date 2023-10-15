package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Statistic;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleStatistic;
import com.pjatk.turtlegame.repositories.StatisticRepository;
import com.pjatk.turtlegame.repositories.TurtleStaticsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TurtleStatisticService {

    TurtleStaticsRepository turtleStaticsRepository;
    StatisticRepository statisticRepository;

    @Transactional
    public void addBasicStats(Turtle turtle) {
        for (Statistic statistic : statisticRepository.findAll()) {
            TurtleStatistic turtleStatistic = new TurtleStatistic();
            turtleStatistic.setTurtle(turtle);
            turtleStatistic.setStatistic(statistic);
            turtleStatistic.setValue(0);
            turtleStaticsRepository.save(turtleStatistic);
        }
    }

}
