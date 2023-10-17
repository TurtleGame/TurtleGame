package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


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
        boolean isOnline = userService.isUserOnline(user);
        model.addAttribute("userInformation", user);
        model.addAttribute("turtles", user.getTurtles());
        model.addAttribute("isOnline", isOnline);
        return "pages/userPage";
    }

    @GetMapping(path = "/edit")
    public String getEditPage(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("user", userRepository.findById(turtleUserDetails.getId()));
        return "pages/editPage";
    }

    @PostMapping(path = "/edit-info")
    public String changeAboutInfo(@RequestParam("content") String content,
                                  @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());

        userService.editAbout(content, user);
        return "pages/editPage";
    }

    @GetMapping("/search-by-keyword")
    public ResponseEntity<List<String>> searchUsers(@RequestParam("keyword") String keyword, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {


        return ResponseEntity.ok(userService.searchUsers(keyword, turtleUserDetails.getUsername()));
    }

    @PostMapping(path = "/edit-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                 Model model) {
        User user = userRepository.findById(turtleUserDetails.getId());

        if (!userService.changePassword(user, oldPassword, newPassword)) {
            model.addAttribute("changePasswordFailedMessage", "Zmiana hasła nieudana!");
            return "pages/editPage";
        }

        model.addAttribute("changePasswordSuccessMessage", "Zmiana hasła udana");
        return "pages/editPage";
    }
}
