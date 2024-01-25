package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurtleTypeRepository extends JpaRepository<TurtleType, Integer> {
    TurtleType findById(int id);
}
