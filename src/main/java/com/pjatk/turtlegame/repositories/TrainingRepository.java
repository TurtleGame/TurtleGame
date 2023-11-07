package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Training;
import com.pjatk.turtlegame.models.TrainingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {
    Training findById(int id);
}
