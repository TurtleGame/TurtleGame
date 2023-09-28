package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
    User findById(int id);
    User findUserByEmail(String email);
}
