package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/friends")
@AllArgsConstructor
public class FriendsController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("friends", userService.getFriends(user));

        return "pages/friends";
    }

    @PostMapping("/add")
    public String addFriend(@RequestParam("friendUsername") String username,
                            Model model,
                            @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        try {
            userService.sendFriendRequest(user, username);
        } catch (Exception e) {
            model.addAttribute("failedMessage", e.getMessage());
            model.addAttribute("friends", userService.getFriends(user));
            return "pages/friends";
        }
        model.addAttribute("successMessage", "Zaproszenie wysłano pomyślnie!");
        model.addAttribute("friends", userService.getFriends(user));
        return "pages/friends";
    }

    @PostMapping("/{id}/delete")
    public String deleteFriendRequest(@PathVariable int id,
                         Model model,
                         @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        try {
            userService.deleteFromFriendsList(id);
        } catch (Exception e) {
            model.addAttribute("failedMessage", e.getMessage());
            model.addAttribute("friends", userService.getFriends(user));
            return "pages/friends";
        }
        model.addAttribute("successMessage", "Usunięto!");
        model.addAttribute("friends", userService.getFriends(user));
        return "pages/friends";
    }

    @PostMapping("/{id}/accept")
    public String addFriends(@PathVariable int id,
                         Model model,
                         @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        try {
            userService.acceptFriendRequest(id);
        } catch (Exception e) {
            model.addAttribute("failedMessage", e.getMessage());
            model.addAttribute("friends", userService.getFriends(user));
            return "pages/friends";
        }
        model.addAttribute("successMessage", "Dodano znajomego!");
        model.addAttribute("friends", userService.getFriends(user));
        return "pages/friends";
    }

}
