package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.Turtle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
