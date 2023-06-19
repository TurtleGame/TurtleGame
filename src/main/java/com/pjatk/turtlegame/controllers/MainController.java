package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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

    @GetMapping(path = "/main")
    public String testIndex(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("nick", turtleUserDetails.getUsername());
        return "pages/main";
    }

    @GetMapping("/logout")
    public String wyloguj(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, null, authentication);
        }
        return "redirect:/index";
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
            return "redirect:/main";
        }
        return "pages/error";

    }
}
