package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Statistic;
import com.pjatk.turtlegame.repositories.StatisticRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class StatisticService {
  private final StatisticRepository statisticRepository;
  private final TurtleService turtleService;

  public Statistic getStatisticByName(String name){
    return statisticRepository.findByName(name).orElseThrow(null);
  }




}
