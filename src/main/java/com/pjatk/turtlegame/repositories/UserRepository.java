package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
    User findById(int id);

}