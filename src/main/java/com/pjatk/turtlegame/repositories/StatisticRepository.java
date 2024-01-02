package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticRepository extends JpaRepository<Statistic, Integer> {
    Optional<Statistic> findByName(String name);
    Statistic findById(int id);
}
