package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.AchievementsRepoistory;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.AchievementsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/achievements")
@AllArgsConstructor
public class AchievementsController {
    private final AchievementsRepoistory achievementsRepoistory;

    UserRepository userRepository;
    AchievementsService achievementsService;

    @GetMapping
    public String achievementIndex(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                   RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            achievementsService.checkAchievements(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
        }

        model.addAttribute("achievements", achievementsRepoistory.findAll());
        model.addAttribute("context", "achievements");
        return "pages/achievements";
    }
}
