package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    public MainController(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String index() {
        return "pages/index";
    }

    @GetMapping(path = "/test")
    public String testIndex(Model model) {
        return "pages/test";
    }

    @GetMapping(path = "/error")
    public String errorIndex() {
        return "pages/error";
    }

    @GetMapping(path = "/login")
    String login() {
        return "pages/index";
    }

    @PostMapping(path = "/login")
    public String processLogin(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userRepository.findUserByUsername(username);
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return "redirect:/test";
        }
        return "redirect:/error";

    }
}
