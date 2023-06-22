package com.pjatk.turtlegame.config;

import com.pjatk.turtlegame.models.TurtleExpeditionHistory;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.PrivateMessageService;
import com.pjatk.turtlegame.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import com.pjatk.turtlegame.models.Turtle;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class MyInterceptor implements HandlerInterceptor {


    private final UserRepository userRepository;
    private final UserService userService;
    private final TurtleRepository turtleRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;
    PrivateMessageService privateMessageService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!request.getMethod().equalsIgnoreCase("GET")) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return true;
        }

        User user = this.userRepository.findUserByUsername(authentication.getName());
        if (user == null) {
            return true;
        }

        List<Turtle> turtleList = userService.getTurtles(user);
        for (Turtle turtle : turtleList) {
            if (turtle.isAvailable()) {
                continue;
            }

            if (!turtleExpeditionHistoryRepository.existsByTurtleAndEndAtAfter(turtle, LocalDateTime.now())) {
                turtle.setAvailable(true);
                turtleRepository.save(turtle);
            }
        }

        List<TurtleExpeditionHistory> turtleExpeditionHistoryList = turtleExpeditionHistoryRepository.findAll();

        for (TurtleExpeditionHistory history : turtleExpeditionHistoryList) {
            if (!history.isWasRewarded() && history.getEndAt().isBefore(LocalDateTime.now())) {
                user.setGold((int) (user.getGold() + history.getExpedition().getGold()));
                System.out.println("User" + user.getGold());
                history.setWasRewarded(true);
                userRepository.save(user);
                turtleExpeditionHistoryRepository.save(history);
                privateMessageService.sendReport(user.getId(), history.getTurtle().getId());
            }
        }

        return true;
    }
}
