package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;

import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String indexLogin(
            @RequestParam(name = "error", required = false) String error,
            @ModelAttribute("userDTO") UserDTO userDTO,
            Model model,
            @AuthenticationPrincipal TurtleUserDetails turtleUserDetails
    ) {

        if (turtleUserDetails != null) {
            return "pages/main";
        }

        model.addAttribute("context", "login");

        if (error != null) {
            model.addAttribute("errorMessage", "Niepoprawna nazwa użytkownika lub hasło");
        }

        return "pages/index";
    }

    @GetMapping(path = {"/registration"})
    public String indexRegister(
            @RequestParam(name = "error", required = false) String error,
            @ModelAttribute("userDTO") UserDTO userDTO,
            Model model) {

        model.addAttribute("context", "register");

        return "pages/index";
    }


    @PostMapping("/registration")
    public String registration(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult bindingResult, Model model) {

        model.addAttribute("context", "register");

        if (userService.isUsernameAlreadyTaken(userDTO.getUsername())) {
            bindingResult.rejectValue("username", "error.userDTO", "Nazwa użytkownika jest już zajęta!");
        }

        if (userService.isEmailAlreadyTaken(userDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.userDTO", "Email jest już zajęty!");
        }

        if (bindingResult.hasErrors()) {
            return "pages/index";
        }

        userService.addNewUser(userDTO);
        model.addAttribute("registrationSuccessMessage", "Rejestracja zakończona pomyślnie. Możesz teraz się zalogować.");
        model.addAttribute("context", "login");
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

}
