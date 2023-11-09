package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.FriendRequest;
import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

    Optional<FriendRequest> findById(int id);

    List<FriendRequest> findBySenderOrReceiver(User sender, User receiver);
}
