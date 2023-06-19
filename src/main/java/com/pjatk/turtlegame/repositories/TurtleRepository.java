package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Turtle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurtleRepository extends JpaRepository<Turtle, Integer> {
    Turtle findById(int id);
}
