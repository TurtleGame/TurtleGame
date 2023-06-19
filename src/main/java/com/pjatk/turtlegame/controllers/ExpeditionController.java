package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.ExpeditionRepository;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/expeditions")
@AllArgsConstructor
public class ExpeditionController {

    ExpeditionRepository expeditionRepository;
    TurtleRepository turtleRepository;
    UserRepository userRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;
    TurtleOwnerHistoryRepository TurtleOwnerHistoryRepository;

    @GetMapping(path = "")
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findUserByUsername(turtleUserDetails.getUsername());
        List<TurtleOwnerHistory> turtleOwnerHistoryList = TurtleOwnerHistoryRepository.findByUserAndEndAtIsNull(user);

        model.addAttribute("turtleExpeditionForm", new TurtleExpeditionForm());
        model.addAttribute("expeditions", expeditionRepository.findAll());
        model.addAttribute("turtles", turtleOwnerHistoryList);
        return "pages/expedition";
    }

    @PostMapping(path = "")
    public String send(Model model,
                       @Valid @ModelAttribute("turtleExpeditionForm") TurtleExpeditionForm turtleExpeditionForm,
                       @RequestParam("turtle") int turtleId,
                       @RequestParam("expedition") int expeditionId,
                       @RequestParam("durationTime") int time,
                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "pages/expedition";
        }

        // Pobierz żółwia i wyprawę na podstawie przekazanych identyfikatorów
        Turtle turtle = turtleRepository.findById(turtleId);
        Expedition expedition = expeditionRepository.findById(expeditionId);

        // Ustaw żółwia, wyprawę i czas w obiekcie turtleExpedition
        TurtleExpeditionHistory turtleExpedition = new TurtleExpeditionHistory();
        turtleExpedition.setTurtle(turtle);
        turtleExpedition.setExpedition(expedition);
        turtleExpedition.setStartAt(LocalDateTime.now());
        turtleExpedition.setEndAt(turtleExpedition.getStartAt().plusMinutes(time));

        turtleExpeditionHistoryRepository.save(turtleExpedition);
        return "pages/test";
    }
}