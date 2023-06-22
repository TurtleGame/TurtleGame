package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Turtle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurtleRepository extends JpaRepository<Turtle, Integer> {
    Turtle findById(int id);
}
