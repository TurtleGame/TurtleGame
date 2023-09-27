package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.DTOs.EggsForm;
import com.pjatk.turtlegame.models.DTOs.FeedTurtleDTO;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ItemService;
import com.pjatk.turtlegame.services.TurtleEggService;
import com.pjatk.turtlegame.services.TurtleService;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/nest")
@AllArgsConstructor
public class NestController {
    UserRepository userRepository;
    UserService userService;
    TurtleService turtleService;
    ItemService itemService;
    TurtleEggService turtleEggService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "nest");
        model.addAttribute("eggs", itemService.getEggs(turtleUserDetails.getId()));
        model.addAttribute("eggsForm", new EggsForm());

        return "pages/nest";
    }

    @PostMapping("/{id}/delete")
    public String abandonEgg(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws Exception {
        itemService.abandonEgg(turtleUserDetails.getId(), id);
        return "redirect:/nest";
    }

    @PostMapping("/{id}/adopt")
    public String adoptEgg(@ModelAttribute("eggsForm") EggsForm eggsForm,
                           @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                           @PathVariable int id,
                           @RequestParam("Name") String name,
                           Model model,
                           BindingResult bindingResult) throws Exception {
        //TODO
        if (name.length() < 2 || name.length() > 50) {
            bindingResult.rejectValue("Name", "error.wrongName", "Nieprawidłowe imię");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("eggs", itemService.getEggs(turtleUserDetails.getId()));

            return "pages/nest";
        }

        itemService.adoptEgg(turtleUserDetails.getId(), id, name);
        return "redirect:/nest";
    }

    @PostMapping("/{id}/warm")
    public String warmEgg(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws Exception {
        turtleEggService.warmEgg(turtleUserDetails.getId(), id);
        return "redirect:/nest";
    }
}