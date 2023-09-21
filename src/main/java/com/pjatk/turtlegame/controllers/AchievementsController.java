package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.repositories.AchievementsRepoistory;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/achievements")
@AllArgsConstructor
public class AchievementsController {
    private final AchievementsRepoistory achievementsRepoistory;

    @GetMapping
    public String achievementIndex(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("achievements", achievementsRepoistory.findAll());
        model.addAttribute("context", "achievements");
        return "pages/achievements";
    }
}
