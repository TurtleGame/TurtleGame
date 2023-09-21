package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepository extends JpaRepository<Statistic, Integer> {
}
