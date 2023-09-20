package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleEgg;
import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurtleEggRepository extends JpaRepository<TurtleEgg, Integer> {
    List<TurtleEgg> findByUser(User user);
}
