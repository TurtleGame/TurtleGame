package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Guard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardsRepository extends JpaRepository<Guard, Integer> {
}
