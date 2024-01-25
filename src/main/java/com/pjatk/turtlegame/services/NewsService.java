package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.News;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.NewsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public void addNews(User user, String title, String content){

        News news = new News();
        news.setAuthor(user);
        news.setTitle(title);
        news.setContent(content);
        news.setReleaseDate(LocalDateTime.now());

        newsRepository.save(news);

    }

    public List<News> getAll(){
        Sort sort = Sort.by(Sort.Direction.DESC, "releaseDate");
        return newsRepository.findAll(sort);
    }

    public void editNews(String title, String content, int id){

        News news = newsRepository.findById(id).orElseThrow();

        news.setContent(content);
        news.setTitle(title);
        newsRepository.save(news);

    }


}
