package com.pjatk.turtlegame.repositories;

import com.pjatk.turtlegame.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Integer> {
}
