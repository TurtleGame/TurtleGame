package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.ItemStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemStatisticRepository extends JpaRepository<ItemStatistic, Integer> {

    List<ItemStatistic> findAllByItemId(int itemId);
}
