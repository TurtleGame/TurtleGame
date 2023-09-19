package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
