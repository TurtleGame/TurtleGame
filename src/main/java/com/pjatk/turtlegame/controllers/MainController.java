package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;

import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.services.NewsService;
import com.pjatk.turtlegame.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/")
@AllArgsConstructor
public class MainController {

    @Autowired
    private UserService userService;
    private final NewsService newsService;


    @GetMapping(path = {"", "/login"})
    public String indexLogin(
            HttpServletRequest request,
            @ModelAttribute("userDTO") UserDTO userDTO,
            Model model,
            @AuthenticationPrincipal TurtleUserDetails turtleUserDetails
    ) {

        if (turtleUserDetails != null) {
            model.addAttribute("context", "home");
            model.addAttribute("news", newsService.getAll());
            return "pages/main";
        }
        model.addAttribute("context", "login");


        Object authenticationException = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (authenticationException instanceof AuthenticationException authException) {
            model.addAttribute("failedMessage", authException.getMessage());
            request.getSession().invalidate();
        }

        return "pages/index";
    }


    @GetMapping(path = {"/registration"})
    public String indexRegister() {
        return "redirect:/";
    }

    @PostMapping("/remind-password")
    public String sendLinkToChangePassword(@RequestParam("email") String email, Model model, RedirectAttributes redirectAttributes) {
        userService.sendChangePasswordMail(email);
        model.addAttribute("context", "login");
        redirectAttributes.addFlashAttribute("successMessage", "Mail do odzyskania hasła wysłany!");
        return "redirect:/";
    }

    @GetMapping("/change-password")
    public String changePasswordIndex(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "pages/changePassword";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("token") String token, @RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.changePasswordFromMail(token, password, confirmPassword);
        } catch (Exception e) {
            model.addAttribute("token", token);
            model.addAttribute("failedMessage", e.getMessage());
            return "pages/changePassword";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Hasło zmienione. Możesz teraz się zalogować.");
        model.addAttribute("context", "login");
        return "redirect:/";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userDTO") @Valid UserDTO userDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

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
        try {
            userService.addNewUser(userDTO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", "Wystąpił błąd");
        }
        redirectAttributes.addFlashAttribute("successMessage", "Rejestracja zakończona pomyślnie. Na mailu czeka na Ciebie link aktywacyjny.");
        model.addAttribute("context", "login");
        return "redirect:/";
    }


    @GetMapping("/registration/confirm")
    public String confirm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.confirmToken(token);
        } catch (Exception e) {
            model.addAttribute("failedMessage", e.getMessage());
        }
        redirectAttributes.addFlashAttribute("successMessage", "Konto aktywowane. Możesz się zalogować.");
        return "redirect:/";
    }

    @GetMapping(path = "/main")
    public String mainPage(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "home");
        model.addAttribute("news", newsService.getAll());
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


}
