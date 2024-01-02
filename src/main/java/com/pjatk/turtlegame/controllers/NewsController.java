package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.repositories.NewsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.NewsDTO;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/news")
@AllArgsConstructor
public class NewsController {
    private final UserRepository userRepository;
    private final NewsService newsService;


    @PostMapping("/create")
    public String createNews(@ModelAttribute("newsDTO") NewsDTO newsDTO,
                             @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                             RedirectAttributes redirectAttributes,
                             Model model
    ) {
        User user = userRepository.findById(turtleUserDetails.getId());
        newsService.addNews(user, newsDTO.getTitle(), newsDTO.getContent());

        model.addAttribute("news", newsService.getAll());
        redirectAttributes.addFlashAttribute("successMessage", "Dodałeś ogłoszenie!");
        return "redirect:/main";
    }

    @PostMapping("/edit")
    public String editNews(@RequestParam("edit-title") String editTitle,
                           @RequestParam("edit-content") String editContent,
                           @RequestParam("news-id") int id,
                           @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        newsService.editNews(editTitle, editContent, id);
        model.addAttribute("news", newsService.getAll());
        redirectAttributes.addFlashAttribute("successMessage", "Wprowadziłeś zmiany!");
        return "redirect:/main";
    }


}
