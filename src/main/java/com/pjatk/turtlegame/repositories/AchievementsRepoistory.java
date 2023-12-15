package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementsRepoistory extends JpaRepository<Achievement, Integer> {

    List<Achievement> findAll();
    Achievement findById(int achievementId);
}
