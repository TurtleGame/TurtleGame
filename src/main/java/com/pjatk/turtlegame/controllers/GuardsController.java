package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.GuardsRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guards")
@AllArgsConstructor
public class GuardsController {
    private final UserRepository userRepository;
    private final GuardsRepository guardsRepository;

    @GetMapping()
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "guards");
        model.addAttribute("guards", guardsRepository.findAll());

        return "pages/guards";
    }
}
