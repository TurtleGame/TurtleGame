package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserItemRepository extends JpaRepository<UserItem, Integer> {
}
