package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.TurtleTrainingForm;
import com.pjatk.turtlegame.repositories.TrainingItemRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.repositories.TurtleTrainingHistoryRepository;
import com.pjatk.turtlegame.repositories.TrainingRepository;
import com.pjatk.turtlegame.services.AcademyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;


@Controller
@RequestMapping(path = "/academy")
@AllArgsConstructor
public class AcademyController {
    UserRepository userRepository;
    TrainingRepository trainingRepository;
    TrainingItemRepository trainingItemRepository;
    TurtleTrainingHistoryRepository turtleTrainingHistoryRepository;
    AcademyService academyService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());

        model.addAttribute("context", "academy");
        model.addAttribute("trainings", trainingRepository.findAll());
        model.addAttribute("turtleTrainingForm", new TurtleTrainingForm());
        model.addAttribute("user", user);
        model.addAttribute("academyService", academyService);

        return "pages/academy";
    }

    @PostMapping(path = "")
    public String send(@Valid @ModelAttribute("turtleTrainingForm") TurtleTrainingForm turtleTrainingForm,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                       Model model,
                       RedirectAttributes redirectAttributes
    ) throws Exception {

        model.addAttribute("context", "academy");
        model.addAttribute("trainings", trainingRepository.findAll());
        model.addAttribute("turtleTrainingForm", turtleTrainingForm);

        if (bindingResult.hasErrors()) {
            return "pages/academy";
        }

        Turtle turtle = turtleTrainingForm.getTurtle();
        Training training = turtleTrainingForm.getTraining();
        if (turtle.getOwner() == null || turtle.getOwner().getId() != turtleUserDetails.getId()) {
            bindingResult.rejectValue("turtle", "error.nullTurtle", "Nie znaleziono żółwia.");

            return "pages/academy";
        }
        if (turtleTrainingHistoryRepository.existsByTurtleAndEndAtAfter(turtle, LocalDateTime.now())) {
            bindingResult.rejectValue("durationTime", "error.alreadyOnExpedition", "Żółw już trenuje.");

            return "pages/academy";
        }

        try {
            academyService.turtleTraining(turtle, training, turtleTrainingForm.getDurationTime());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
        }

        return "redirect:/academy";
    }

    @GetMapping("/if-training-can")
    public ResponseEntity<?> ifTrainingCan(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                           @RequestParam(name = "trainingId") Integer trainingId,
                                           @RequestParam(name = "duration") int durationTime) {
        User user = userRepository.findById(turtleUserDetails.getId());
        Training training = trainingRepository.findById(trainingId).orElseThrow();
        System.out.println(durationTime);
        boolean canTraining = academyService.ifTrainingCan(user, training, durationTime);

        return ResponseEntity.ok(canTraining);
    }
}
