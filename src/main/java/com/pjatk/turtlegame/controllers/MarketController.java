package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.MarketService;
import com.pjatk.turtlegame.services.TurtleService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/market")
@AllArgsConstructor
public class MarketController {
    private MarketService marketService;
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("context", "market");
        model.addAttribute("turtles", marketService.getAll());
        model.addAttribute("marketService", marketService);

        return "pages/market";
    }

    @PostMapping("/{id}/buy")
    public String buyTurtle(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws Exception {
        User user = userRepository.findById(turtleUserDetails.getId());
        marketService.buyTurtle(id, user);

        return "redirect:/market";
    }
}
