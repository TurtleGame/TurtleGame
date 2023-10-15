package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping(path = "/{id}")
    public String getUserPage(Model model, @PathVariable int id) {
        User user = userRepository.findById(id);
        model.addAttribute("userInformation", user);
        model.addAttribute("turtles", user.getTurtles());
        return "pages/userPage";
    }

    @GetMapping(path = "/{id}/edit")
    public String getEditPage(Model model, @PathVariable int id) {
        model.addAttribute("user", userRepository.findById(id));
        return "pages/editPage";
    }

    @GetMapping("/search-by-keyword")
    public ResponseEntity<List<String>> searchUsers(@RequestParam("keyword") String keyword, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {


        return ResponseEntity.ok(userService.searchUsers(keyword, turtleUserDetails.getUsername()));
    }
}
