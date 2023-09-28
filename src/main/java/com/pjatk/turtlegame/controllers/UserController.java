package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping(path = "/{id}")
    public String getUserPage(Model model, @PathVariable int id) {
        model.addAttribute("userInformation", userRepository.findById(id));
        model.addAttribute("turtles", userService.getTurtles(userRepository.findById(id)));
        return "pages/userPage";
    }

    @GetMapping(path = "/{id}/edit")
    public String getEditPage(Model model, @PathVariable int id) {
        model.addAttribute("user", userRepository.findById(id));
        return "pages/editPage";
    }
}
