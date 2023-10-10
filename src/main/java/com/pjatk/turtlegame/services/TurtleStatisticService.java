package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleStatistic;
import com.pjatk.turtlegame.repositories.TurtleStaticsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TurtleStatisticService {

    StatisticService statisticService;
    TurtleStaticsRepository turtleStaticsRepository;

    @Transactional
    public void addBasicStats(Turtle turtle) {

        TurtleStatistic hp = new TurtleStatistic();
        hp.setTurtle(turtle);
        hp.setStatistic(statisticService.getStatisticByName("HP"));
        hp.setValue(0);
        turtleStaticsRepository.save(hp);

        TurtleStatistic mp = new TurtleStatistic();
        mp.setTurtle(turtle);
        mp.setStatistic(statisticService.getStatisticByName("MP"));
        mp.setValue(0);
        turtleStaticsRepository.save(mp);

        TurtleStatistic agility = new TurtleStatistic();
        agility.setTurtle(turtle);
        agility.setStatistic(statisticService.getStatisticByName("Zręczność"));
        agility.setValue(0);
        turtleStaticsRepository.save(agility);

        TurtleStatistic strength = new TurtleStatistic();
        strength.setTurtle(turtle);
        strength.setStatistic(statisticService.getStatisticByName("Siła"));
        strength.setValue(0);
        turtleStaticsRepository.save(strength);

        TurtleStatistic durability = new TurtleStatistic();
        durability.setTurtle(turtle);
        durability.setStatistic(statisticService.getStatisticByName("Wytrzymałość"));
        durability.setValue(0);
        turtleStaticsRepository.save(durability);
    }

}
