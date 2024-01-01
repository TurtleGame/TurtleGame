package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.FriendRequestService;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/friends")
@AllArgsConstructor
public class FriendsController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final FriendRequestService friendRequestService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "friends");
        model.addAttribute("friends", userService.getFriends(user));

        return "pages/friends";
    }

    @PostMapping("/add")
    public String addFriend(@RequestParam(value = "friendUsername", required = false) String username,
                            Model model,
                            @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                            RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());
        try {
            friendRequestService.sendFriendRequest(user, username);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            model.addAttribute("friends", userService.getFriends(user));
            return "redirect:/friends";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Zaproszenie wysłano pomyślnie!");
        model.addAttribute("friends", userService.getFriends(user));
        return "redirect:/friends";
    }

    @PostMapping("/{id}/delete")
    public String deleteFriendRequest(@PathVariable(required = false) int id,
                                      Model model,
                                      @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                      RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());
        try {
            friendRequestService.deleteFromFriendsList(id, user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            model.addAttribute("friends", userService.getFriends(user));
            return "redirect:/friends";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Usunięto!");
        model.addAttribute("friends", userService.getFriends(user));
        return "redirect:/friends";
    }

    @PostMapping("/{id}/accept")
    public String addFriends(@PathVariable int id,
                             Model model,
                             @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                             RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());
        try {
            friendRequestService.acceptFriendRequest(id, user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            model.addAttribute("friends", userService.getFriends(user));
            return "redirect:/friends";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Dodano znajomego!");
        model.addAttribute("friends", userService.getFriends(user));
        return "redirect:/friends";
    }

}
