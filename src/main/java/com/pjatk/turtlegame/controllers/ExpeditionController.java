package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.ExpeditionRepository;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ExpeditionHistoryService;
import com.pjatk.turtlegame.services.ExpeditionService;
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
    ExpeditionService expeditionService;
    ExpeditionHistoryService expeditionHistoryService;

    @GetMapping(path = "")
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "expeditions");
        model.addAttribute("turtleExpeditionForm", new TurtleExpeditionForm());
        model.addAttribute("expeditions", expeditionRepository.findAll());

        return "pages/expedition";
    }

    @PostMapping(path = "")
    public String send(Model model,
                       @Valid @ModelAttribute("turtleExpeditionForm") TurtleExpeditionForm turtleExpeditionForm,
                       @RequestParam("turtle") int turtleId,
                       @RequestParam("expedition") int expeditionId,
                       @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                       BindingResult bindingResult) throws Exception {

        model.addAttribute("turtleExpeditionForm", turtleExpeditionForm);
        model.addAttribute("expeditions", expeditionRepository.findAll());
        model.addAttribute("turtles", userService.getTurtles(turtleUserDetails.getUser()));
        model.addAttribute("context", "expeditions");

        if (bindingResult.hasErrors()) {
            return "pages/expedition";
        }

        Turtle turtle = turtleRepository.findById(turtleId).orElseThrow(() -> new Exception("Turtle not found"));
        Expedition expedition = expeditionRepository.findById(expeditionId);

        if (turtle.getLevel() < expedition.getMinLevel()) {
            bindingResult.rejectValue("durationTime", "error.levelTooLow", "Wymagany level, aby wyruszyć na tą wyprawę to " + expedition.getMinLevel());

            return "pages/expedition";
        }
        if (turtleExpeditionHistoryRepository.existsByTurtleAndEndAtAfter(turtle, LocalDateTime.now())) {
            bindingResult.rejectValue("durationTime", "error.alreadyOnExpedition", "Żółw jest juz na wyprawie.");

            return "pages/expedition";
        }
        turtle.setAvailable(false);
        turtleRepository.save(turtle);
        turtleExpeditionHistoryRepository
                .save(expeditionService.turtleExpedition(turtle, expedition, turtleExpeditionForm.getDurationTime()));

        return "redirect:/expeditions";
    }
}