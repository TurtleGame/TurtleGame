package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Expedition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpeditionRepository extends JpaRepository<Expedition, Integer> {
    Expedition findById(int id);
}
