package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.ItemOwnerMarket;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemOwnerMarketRepository extends JpaRepository<ItemOwnerMarket, Integer> {
    ItemOwnerMarket findByItemIdAndEndAtIsNull(int itemId);
    ItemOwnerMarket findByItemIdAndUserIdAndEndAtIsNull(int itemId, int ownerId);
    List<ItemOwnerMarket> findAllByItemIdAndEndAtIsNull(int itemId);
    List<ItemOwnerMarket> findAllByItemIdAndUserId(int itemId, int ownerId);
}
