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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Objects;


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
                       @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                       Model model,
                       RedirectAttributes redirectAttributes
    ) throws Exception {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "expeditions");
        model.addAttribute("turtleExpeditionForm", turtleExpeditionForm);
        model.addAttribute("expeditions", expeditionRepository.findAll());

        if (turtleExpeditionForm.getTurtleId() == null) {
            redirectAttributes.addFlashAttribute("failedMessage", "Musisz wybrać żółwia");
            return "redirect:/expeditions";
        }

        if (turtleExpeditionForm.getTurtleId().equals("*")) {
            try {
                expeditionService.sendAllTurtlesOnExpedition(user, turtleExpeditionForm.getExpeditionId(), turtleExpeditionForm.getDurationTime());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            }
        } else {
            try {
                expeditionService.turtleExpedition(Integer.parseInt(turtleExpeditionForm.getTurtleId()), turtleExpeditionForm.getExpeditionId(), turtleExpeditionForm.getDurationTime(), user);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            }
        }
        return "redirect:/expeditions";
    }

}