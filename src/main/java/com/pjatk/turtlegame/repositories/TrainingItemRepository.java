package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.TrainingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingItemRepository extends JpaRepository<TrainingItem, Integer> {
    TrainingItem findById(int id);
}
