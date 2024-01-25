package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TurtleEgg;
import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurtleEggRepository extends JpaRepository<TurtleEgg, Integer> {
    List<TurtleEgg> findByUser(User user);

    Optional<TurtleEgg> findByIdAndUserId(int eggId, int userId);
}
