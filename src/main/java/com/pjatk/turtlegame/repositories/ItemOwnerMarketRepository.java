package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.ItemOwnerMarket;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemOwnerMarketRepository extends JpaRepository<ItemOwnerMarket, Integer> {
}
