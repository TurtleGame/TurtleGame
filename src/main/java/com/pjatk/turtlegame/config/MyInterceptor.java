package com.pjatk.turtlegame.config;

import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
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
    TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;


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
        for(Turtle turtle : turtleList){
            if(!turtleExpeditionHistoryRepository.existsByTurtleAndEndAtAfter(turtle, LocalDateTime.now())){
                turtle.setAvailable(true);
            }
        }

        return true;
    }
}
