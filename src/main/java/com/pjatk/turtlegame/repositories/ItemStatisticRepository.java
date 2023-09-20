package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.ItemStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemStatisticRepository extends JpaRepository<ItemStatistic, Integer> {

    ItemStatistic findItemStatisticByItem_Id(int id);
}
