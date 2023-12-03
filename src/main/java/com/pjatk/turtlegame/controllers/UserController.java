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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping(path = "/{id}")
    public String getUserPage(Model model, @PathVariable int id, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(id);
        User loggedUser = userRepository.findById(turtleUserDetails.getId());
        if(user == null){
            return "redirect:/main";
        }

        boolean isOnline = userService.isUserOnline(user);
        model.addAttribute("isFriends", userService.isUserOnFriendList(loggedUser, user));
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
        return "redirect:/user/edit";
    }

    @GetMapping("/search-by-keyword")
    public ResponseEntity<List<String>> searchUsers(@RequestParam("keyword") String keyword, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {


        return ResponseEntity.ok(userService.searchUsers(keyword, turtleUserDetails.getUsername()));
    }

    @PostMapping(path = "/edit-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            userService.changePassword(user, oldPassword, newPassword);
        } catch (Exception e) {
            model.addAttribute("failedMessage", e.getMessage());
            return "pages/editPage";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Zmiana hasła udana");
        return "redirect:/user/edit";
    }

    @PostMapping(path = "/change-username")
    public String changeUsername(@RequestParam("newUsername") String username,
                                 @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                 Model model) {

        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            userService.changeUsername(user, username);
        } catch (Exception e) {
            model.addAttribute("failedMessage", e.getMessage());
            return "pages/editPage";
        }

        return "redirect:/logout";
    }

    @PostMapping(path = "change-avatar")
    public String changeAvatar(@RequestParam("avatar") MultipartFile avatar,
                               @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(turtleUserDetails.getId());
        try {
            userService.changeAvatar(user, avatar);
        } catch (IOException e) {
            model.addAttribute("failedMessage", e.getMessage());
            return "pages/editPage";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Avatar pomyślnie dodany!");
        return "redirect:/user/edit";
    }
}

