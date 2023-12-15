package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.Achievement;
import com.pjatk.turtlegame.models.AchievementsEarned;
import com.pjatk.turtlegame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementsEarnedRepoistory extends JpaRepository<AchievementsEarned, Integer> {

    Optional<AchievementsEarned> findByUserAndAchievement(User user, Achievement achievement);
}
