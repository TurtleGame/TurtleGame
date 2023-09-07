package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;

import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/")
public class MainController {

    @Autowired
    private UserService userService;


    @GetMapping(path = {"", "/login"})
    public String index(
            @RequestParam(name = "error", required = false) String error,
            @ModelAttribute("userDTO") UserDTO userDTO,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Niepoprawna nazwa użytkownika lub hasło");
        }


        return "pages/index";
    }

    @GetMapping(path = "/main")
    public String mainPage(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "home");
        return "pages/main";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
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

    @PostMapping("/registration")
    public String registration(@ModelAttribute("user") @Valid UserDTO userDTO, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            model.addAttribute("userDTO", userDTO);
           return "pages/index";
        }
        userService.addNewUser(userDTO);
        return "redirect:/index";
    }


}
