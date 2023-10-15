package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.TurtleExpeditionForm;
import com.pjatk.turtlegame.repositories.ExpeditionRepository;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
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

    @GetMapping(path = "")
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "expeditions");
        model.addAttribute("turtleExpeditionForm", new TurtleExpeditionForm());
        model.addAttribute("expeditions", expeditionRepository.findAll());

        return "pages/expedition";
    }

    @PostMapping(path = "")
    public String send(@Valid @ModelAttribute("turtleExpeditionForm") TurtleExpeditionForm turtleExpeditionForm,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                       Model model
                    ) throws Exception {

        model.addAttribute("context", "expeditions");
        model.addAttribute("turtleExpeditionForm", turtleExpeditionForm);
        model.addAttribute("expeditions", expeditionRepository.findAll());

        if (bindingResult.hasErrors()) {
            return "pages/expedition";
        }

        Turtle turtle = turtleExpeditionForm.getTurtle();
        Expedition expedition = turtleExpeditionForm.getExpedition();
        if (turtle.getOwner() == null || turtle.getOwner().getId() != turtleUserDetails.getId()) {
            bindingResult.rejectValue("turtle", "error.nullTurtle", "Nie znaleziono żółwia.");

            return "pages/expedition";
        }
        if (turtle.getLevel() < expedition.getMinLevel()) {
            bindingResult.rejectValue("expedition", "error.levelTooLow", "Wymagany level, aby wyruszyć na tą wyprawę to " + expedition.getMinLevel());

            return "pages/expedition";
        }
        if (turtleExpeditionHistoryRepository.existsByTurtleAndEndAtAfter(turtle, LocalDateTime.now())) {
            bindingResult.rejectValue("durationTime", "error.alreadyOnExpedition", "Żółw jest juz na wyprawie.");

            return "pages/expedition";
        }

        expeditionService.turtleExpedition(turtle, expedition, turtleExpeditionForm.getDurationTime());

        return "redirect:/expeditions";
    }
}