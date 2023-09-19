package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.services.TurtleService;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "/nest")
@AllArgsConstructor
public class AdoptionController {
    TurtleRepository turtleRepository;
    UserService userService;
    TurtleService turtleService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "nest");

        return "pages/nest";
    }

}