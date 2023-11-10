package com.pjatk.turtlegame.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/market")
@AllArgsConstructor
public class MarketController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("context", "market");

        return "pages/market";
    }
}
