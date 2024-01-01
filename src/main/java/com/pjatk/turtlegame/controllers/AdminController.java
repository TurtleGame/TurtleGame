package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.banFormDTO;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import com.pjatk.turtlegame.models.User;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(path = "/admin-panel")
@AllArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public String adminPanel(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, Model model) {
        User user = userRepository.findById(turtleUserDetails.getId());

        model.addAttribute("context", "admin");

        return "pages/adminPanel";

    }

    @PostMapping(path = "/ban-user")
    public String banUser(@Valid @ModelAttribute("banFormDTO") banFormDTO banFormDTO,
                          @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                          RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            userService.banUser(user, banFormDTO.getPlayerUsername(), banFormDTO.getReason(), banFormDTO.getBanExpireAt().atStartOfDay());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            return "redirect:/main";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Pomyślnie nałożyłeś bana!");
        return "redirect:/main";
    }
}
