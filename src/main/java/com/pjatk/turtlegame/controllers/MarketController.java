package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.MarketService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
        model.addAttribute("turtles", marketService.getAllTurtles());
        model.addAttribute("items", marketService.getAllItems());
        model.addAttribute("eggs", marketService.getAllEggs());
        model.addAttribute("marketService", marketService);

        return "pages/market";
    }

    @PostMapping("/{id}/buyTurtle")
    public String buyTurtle(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.buyTurtle(id, user);

        return "redirect:/market";
    }

    @PostMapping("/{id}/undoTurtle")
    public String undoTurtle(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.undoTurtle(id, user);

        return "redirect:/market";
    }

    @PostMapping("/{id}/buyItem")
    public String buyItem(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.buyItem(id, user);

        return "redirect:/market";
    }

    @PostMapping("/{id}/undoItem")
    public String undoItem(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.undoItem(id, user);

        return "redirect:/market";
    }
}
