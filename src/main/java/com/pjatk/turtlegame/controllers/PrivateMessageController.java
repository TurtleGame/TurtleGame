package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/private-message")
@AllArgsConstructor
public class PrivateMessageController {

    UserRepository userRepository;
    UserService userService;

    @GetMapping(path = "")
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails){
        User user = userRepository.findUserByUsername(turtleUserDetails.getUsername());
        model.addAttribute("nick", turtleUserDetails.getUsername());
        model.addAttribute("gold", user.getGold());
        model.addAttribute("messages", user.getReportList() );
        return "pages/privateMessage";
    }
}
