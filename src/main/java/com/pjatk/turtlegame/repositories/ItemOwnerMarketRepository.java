package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.ItemOwnerMarket;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleOwnerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemOwnerMarketRepository extends JpaRepository<ItemOwnerMarket, Integer> {
    ItemOwnerMarket findByItemIdAndEndAtIsNull(int itemId);
    ItemOwnerMarket findByItemIdAndUserIdAndEndAtIsNull(int itemId, int ownerId);
    Boolean existsByItemIdAndUserId(int itemId, int ownerId);
    List<ItemOwnerMarket> findAllByItemIdAndEndAtIsNull(int itemId);
    List<ItemOwnerMarket> findAllByItemIdAndUserId(int itemId, int ownerId);
}
