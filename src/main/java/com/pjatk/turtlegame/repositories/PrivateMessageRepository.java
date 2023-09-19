package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Integer> {
}
