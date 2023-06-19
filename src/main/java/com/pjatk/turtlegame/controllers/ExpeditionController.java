package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.ExpeditionRepository;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ExpeditionServices;
import com.pjatk.turtlegame.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Controller
@RequestMapping(path = "/expeditions")
@AllArgsConstructor
public class ExpeditionController {

    ExpeditionRepository expeditionRepository;
    TurtleRepository turtleRepository;
    UserRepository userRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;
    TurtleOwnerHistoryRepository TurtleOwnerHistoryRepository;
    UserService userService;
    ExpeditionServices expeditionServices;

    @GetMapping(path = "")
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("nick", turtleUserDetails.getUsername());
        model.addAttribute("turtleExpeditionForm", new TurtleExpeditionForm());
        model.addAttribute("expeditions", expeditionRepository.findAll());
        model.addAttribute("turtles", userService.getTurtles(turtleUserDetails.getUser()));
        return "pages/expedition";
    }

    @PostMapping(path = "")
    public String send(Model model,
                       @Valid @ModelAttribute("turtleExpeditionForm") TurtleExpeditionForm turtleExpeditionForm,
                       @RequestParam("turtle") int turtleId,
                       @RequestParam("expedition") int expeditionId,
                       @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                       BindingResult bindingResult) {

        model.addAttribute("turtleExpeditionForm", turtleExpeditionForm);
        model.addAttribute("expeditions", expeditionRepository.findAll());
        model.addAttribute("turtles", userService.getTurtles(turtleUserDetails.getUser()));

        if (bindingResult.hasErrors()) {
            return "pages/expedition";
        }

        Turtle turtle = turtleRepository.findById(turtleId);
        Expedition expedition = expeditionRepository.findById(expeditionId);

        if (turtleExpeditionHistoryRepository.existsByTurtleAndEndAtAfter(turtle, LocalDateTime.now())) {
            bindingResult.rejectValue("durationTime", "Jest juz na wyprawie.");

            return "pages/expedition";
        }

        turtleExpeditionHistoryRepository
                .save(expeditionServices.turtleExpedition(turtle, expedition, turtleExpeditionForm.getDurationTime()));

        return "main";
    }
}