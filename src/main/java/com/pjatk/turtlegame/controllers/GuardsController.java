package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.BattleResultDTO;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.GuardsRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.BattleService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/guards")
@AllArgsConstructor
public class GuardsController {
    private final UserRepository userRepository;
    private final GuardsRepository guardsRepository;
    private final BattleService battleService;

    @GetMapping()
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "guards");
        model.addAttribute("guards", guardsRepository.findAll());

        return "pages/guards";
    }

    @PostMapping("/attack")
    public String attackGuard(Model model,
                              @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                              @RequestParam(name = "guardId", required = false) Integer guardId,
                              @RequestParam(name = "turtleId", required = false) Integer turtleId,
                              RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "guards");

        if(turtleId == null){
            redirectAttributes.addFlashAttribute("failedMessage", "Musisz wybrać żółwia");
            return "redirect:/guards";
        }
        if(guardId == null){
            redirectAttributes.addFlashAttribute("failedMessage", "Musisz wybrać strażnika");
            return "redirect:/guards";
        }

        try {
            BattleResultDTO battleResult = battleService.processFightWithGuard(user, turtleId, guardId);
            model.addAttribute("battleResult", battleResult);
            model.addAttribute("turtle", user.getTurtle(turtleId));
            model.addAttribute("guard", guardsRepository.findById(guardId).orElseThrow());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            return "redirect:/guards";
        }

        return "pages/battlePage";

    }
}
