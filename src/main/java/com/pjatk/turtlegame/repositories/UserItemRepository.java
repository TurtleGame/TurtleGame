package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Integer> {
    Optional<UserItem> findByItemIdAndUserId(int itemId, int ownerId);
}
