package com.pjatk.turtlegame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @GetMapping
    public String index() {
        return "pages/index";
    }

    @GetMapping(path = "/test")
    public String testIndex(Model model) {
        return "pages/test";
    }
}
