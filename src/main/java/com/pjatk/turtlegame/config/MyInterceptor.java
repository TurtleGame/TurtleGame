package com.pjatk.turtlegame.config;

import com.pjatk.turtlegame.models.TurtleEgg;
import com.pjatk.turtlegame.models.TurtleExpeditionHistory;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleEggRepository;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ExpeditionService;
import com.pjatk.turtlegame.services.PrivateMessageService;
import com.pjatk.turtlegame.services.TurtleStatisticService;
import com.pjatk.turtlegame.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import com.pjatk.turtlegame.models.Turtle;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Component
public class MyInterceptor implements HandlerInterceptor {


    private final UserRepository userRepository;
    private final UserService userService;
    private final TurtleRepository turtleRepository;
    private final TurtleEggRepository turtleEggRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;
    PrivateMessageService privateMessageService;
    private final TurtleStatisticService turtleStatisticService;
    private final ExpeditionService expeditionService;

    @Scheduled(fixedRate = 2 * 60 * 1000)
    @Transactional// Uruchamia się codziennie o północy
    public void resetFedFlag() {
        List<Turtle> allTurtles = turtleRepository.findAll();
        for (Turtle turtle : allTurtles) {
            turtle.setFed(false);
            turtleRepository.save(turtle);
        }
    }

    @Scheduled(fixedRate = 2 * 60 * 1000)
    @Transactional// Uruchamia się codziennie o północy
    public void resetWarming() {
        List<TurtleEgg> eggs = turtleEggRepository.findAll();
        for (TurtleEgg egg : eggs) {
            egg.setWarming(2);
            turtleEggRepository.save(egg);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!request.getMethod().equalsIgnoreCase("GET")) {
            return true;
        }

        User user = getLoggedUser();
        if (user == null) {
            return true;
        }

        handleExpeditions(user);

        for (TurtleEgg egg : user.getEggs()) {
            if (egg.getHatchingAt().isAfter(LocalDateTime.now())) {
                continue;
            }

            if (egg.getHatchingAt().isBefore(LocalDateTime.now()) || egg.getHatchingAt().isEqual(LocalDateTime.now())) {
                Turtle turtle = new Turtle();
                turtle.setAvailable(true);
                turtle.setLevel(0);
                turtle.setName(egg.getName());
                turtle.setUnassignedPoints(0);
                turtle.setTurtleType(egg.getTurtleType());
                turtle.setGender(0);
                turtle.setOwner(user);
                turtle.setEnergy(100);

                turtle.setFed(false);
                turtleRepository.save(turtle);
                turtleStatisticService.addBasicStats(turtle);

                turtleEggRepository.deleteById(egg.getId());
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        User user = getLoggedUser();
        if (user == null) {
            return;
        }
        if (modelAndView != null) {
            modelAndView.addObject("user", user);
        }
    }

    protected User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return this.userRepository.findUserByUsername(authentication.getName());
    }

    protected void handleExpeditions(User user) {

        for (Turtle turtle : user.getTurtles()) {
            if (turtle.isAvailable()) {
                continue;
            }

            if (!turtleExpeditionHistoryRepository.existsByTurtleAndEndAtAfter(turtle, LocalDateTime.now())) {
                turtle.setAvailable(true);
                turtleRepository.save(turtle);
            }
        }

        List<TurtleExpeditionHistory> turtleExpeditionHistoryList = turtleExpeditionHistoryRepository.findAll();
        expeditionService.processTurtleExpeditionHistory(turtleExpeditionHistoryList, user);
    }
}

