package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Turtle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurtleRepository extends JpaRepository<Turtle, Integer> {
    Optional<Turtle> findById(int id);

    Optional<Turtle> findByIdAndOwnerId(int turtleId, int ownerId);

    List<Turtle> findTurtlesByRankingPointsBetween(int minRankingPoints, int maxRankingPoints);
}
