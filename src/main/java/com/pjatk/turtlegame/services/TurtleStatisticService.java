package com.pjatk.turtlegame.services;

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
        TurtleStatistic turtleStatistic1 = new TurtleStatistic();
        TurtleStatistic turtleStatistic2 = new TurtleStatistic();
        TurtleStatistic turtleStatistic3 = new TurtleStatistic();
        TurtleStatistic turtleStatistic4 = new TurtleStatistic();
        TurtleStatistic turtleStatistic5 = new TurtleStatistic();
        switch (turtle.getTurtleType().getId()) {
            case 1:
                turtleStatistic1.setTurtle(turtle);
                turtleStatistic1.setStatistic(statisticRepository.findById(1));
                turtleStatistic1.setValue(8);
                turtleStatistic2.setTurtle(turtle);
                turtleStatistic2.setStatistic(statisticRepository.findById(2));
                turtleStatistic2.setValue(9);
                turtleStatistic3.setTurtle(turtle);
                turtleStatistic3.setStatistic(statisticRepository.findById(3));
                turtleStatistic3.setValue(5);
                turtleStatistic4.setTurtle(turtle);
                turtleStatistic4.setStatistic(statisticRepository.findById(4));
                turtleStatistic4.setValue(8);
                turtleStatistic5.setTurtle(turtle);
                turtleStatistic5.setStatistic(statisticRepository.findById(5));
                turtleStatistic5.setValue(5);
                turtleStaticsRepository.save(turtleStatistic1);
                turtleStaticsRepository.save(turtleStatistic2);
                turtleStaticsRepository.save(turtleStatistic3);
                turtleStaticsRepository.save(turtleStatistic4);
                turtleStaticsRepository.save(turtleStatistic5);
                break;
            case 2:
                turtleStatistic1.setTurtle(turtle);
                turtleStatistic1.setStatistic(statisticRepository.findById(1));
                turtleStatistic1.setValue(7);
                turtleStatistic2.setTurtle(turtle);
                turtleStatistic2.setStatistic(statisticRepository.findById(2));
                turtleStatistic2.setValue(9);
                turtleStatistic3.setTurtle(turtle);
                turtleStatistic3.setStatistic(statisticRepository.findById(3));
                turtleStatistic3.setValue(6);
                turtleStatistic4.setTurtle(turtle);
                turtleStatistic4.setStatistic(statisticRepository.findById(4));
                turtleStatistic4.setValue(9);
                turtleStatistic5.setTurtle(turtle);
                turtleStatistic5.setStatistic(statisticRepository.findById(5));
                turtleStatistic5.setValue(6);
                turtleStaticsRepository.save(turtleStatistic1);
                turtleStaticsRepository.save(turtleStatistic2);
                turtleStaticsRepository.save(turtleStatistic3);
                turtleStaticsRepository.save(turtleStatistic4);
                turtleStaticsRepository.save(turtleStatistic5);
                break;
            case 3:
                turtleStatistic1.setTurtle(turtle);
                turtleStatistic1.setStatistic(statisticRepository.findById(1));
                turtleStatistic1.setValue(6);
                turtleStatistic2.setTurtle(turtle);
                turtleStatistic2.setStatistic(statisticRepository.findById(2));
                turtleStatistic2.setValue(8);
                turtleStatistic3.setTurtle(turtle);
                turtleStatistic3.setStatistic(statisticRepository.findById(3));
                turtleStatistic3.setValue(9);
                turtleStatistic4.setTurtle(turtle);
                turtleStatistic4.setStatistic(statisticRepository.findById(4));
                turtleStatistic4.setValue(9);
                turtleStatistic5.setTurtle(turtle);
                turtleStatistic5.setStatistic(statisticRepository.findById(5));
                turtleStatistic5.setValue(5);
                turtleStaticsRepository.save(turtleStatistic1);
                turtleStaticsRepository.save(turtleStatistic2);
                turtleStaticsRepository.save(turtleStatistic3);
                turtleStaticsRepository.save(turtleStatistic4);
                turtleStaticsRepository.save(turtleStatistic5);
                break;
            case 4:
                turtleStatistic1.setTurtle(turtle);
                turtleStatistic1.setStatistic(statisticRepository.findById(1));
                turtleStatistic1.setValue(5);
                turtleStatistic2.setTurtle(turtle);
                turtleStatistic2.setStatistic(statisticRepository.findById(2));
                turtleStatistic2.setValue(9);
                turtleStatistic3.setTurtle(turtle);
                turtleStatistic3.setStatistic(statisticRepository.findById(3));
                turtleStatistic3.setValue(6);
                turtleStatistic4.setTurtle(turtle);
                turtleStatistic4.setStatistic(statisticRepository.findById(4));
                turtleStatistic4.setValue(8);
                turtleStatistic5.setTurtle(turtle);
                turtleStatistic5.setStatistic(statisticRepository.findById(5));
                turtleStatistic5.setValue(7);
                turtleStaticsRepository.save(turtleStatistic1);
                turtleStaticsRepository.save(turtleStatistic2);
                turtleStaticsRepository.save(turtleStatistic3);
                turtleStaticsRepository.save(turtleStatistic4);
                turtleStaticsRepository.save(turtleStatistic5);
                break;
            case 5:
                turtleStatistic1.setTurtle(turtle);
                turtleStatistic1.setStatistic(statisticRepository.findById(1));
                turtleStatistic1.setValue(12);
                turtleStatistic2.setTurtle(turtle);
                turtleStatistic2.setStatistic(statisticRepository.findById(2));
                turtleStatistic2.setValue(11);
                turtleStatistic3.setTurtle(turtle);
                turtleStatistic3.setStatistic(statisticRepository.findById(3));
                turtleStatistic3.setValue(6);
                turtleStatistic4.setTurtle(turtle);
                turtleStatistic4.setStatistic(statisticRepository.findById(4));
                turtleStatistic4.setValue(9);
                turtleStatistic5.setTurtle(turtle);
                turtleStatistic5.setStatistic(statisticRepository.findById(5));
                turtleStatistic5.setValue(10);
                turtleStaticsRepository.save(turtleStatistic1);
                turtleStaticsRepository.save(turtleStatistic2);
                turtleStaticsRepository.save(turtleStatistic3);
                turtleStaticsRepository.save(turtleStatistic4);
                turtleStaticsRepository.save(turtleStatistic5);
                break;
            case 6:
                turtleStatistic1.setTurtle(turtle);
                turtleStatistic1.setStatistic(statisticRepository.findById(1));
                turtleStatistic1.setValue(9);
                turtleStatistic2.setTurtle(turtle);
                turtleStatistic2.setStatistic(statisticRepository.findById(2));
                turtleStatistic2.setValue(8);
                turtleStatistic3.setTurtle(turtle);
                turtleStatistic3.setStatistic(statisticRepository.findById(3));
                turtleStatistic3.setValue(6);
                turtleStatistic4.setTurtle(turtle);
                turtleStatistic4.setStatistic(statisticRepository.findById(4));
                turtleStatistic4.setValue(11);
                turtleStatistic5.setTurtle(turtle);
                turtleStatistic5.setStatistic(statisticRepository.findById(5));
                turtleStatistic5.setValue(10);
                turtleStaticsRepository.save(turtleStatistic1);
                turtleStaticsRepository.save(turtleStatistic2);
                turtleStaticsRepository.save(turtleStatistic3);
                turtleStaticsRepository.save(turtleStatistic4);
                turtleStaticsRepository.save(turtleStatistic5);
                break;
            case 7:
                turtleStatistic1.setTurtle(turtle);
                turtleStatistic1.setStatistic(statisticRepository.findById(1));
                turtleStatistic1.setValue(20);
                turtleStatistic2.setTurtle(turtle);
                turtleStatistic2.setStatistic(statisticRepository.findById(2));
                turtleStatistic2.setValue(15);
                turtleStatistic3.setTurtle(turtle);
                turtleStatistic3.setStatistic(statisticRepository.findById(3));
                turtleStatistic3.setValue(16);
                turtleStatistic4.setTurtle(turtle);
                turtleStatistic4.setStatistic(statisticRepository.findById(4));
                turtleStatistic4.setValue(19);
                turtleStatistic5.setTurtle(turtle);
                turtleStatistic5.setStatistic(statisticRepository.findById(5));
                turtleStatistic5.setValue(15);
                turtleStaticsRepository.save(turtleStatistic1);
                turtleStaticsRepository.save(turtleStatistic2);
                turtleStaticsRepository.save(turtleStatistic3);
                turtleStaticsRepository.save(turtleStatistic4);
                turtleStaticsRepository.save(turtleStatistic5);
                break;
        }
    }
}
