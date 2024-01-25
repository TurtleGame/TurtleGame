package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.BattleResultDTO;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.BattleService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/arena")
@AllArgsConstructor
public class ArenaController {
    private final UserRepository userRepository;
    private final BattleService battleService;
    private final TurtleRepository turtleRepository;

    @GetMapping()
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "arena");
        model.addAttribute("turtlePairs", battleService.findOpponents(user));

        return "pages/arena";
    }

    @PostMapping("/attack")
    public String attackOtherTurtle(Model model,
                              @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                              @RequestParam(name = "ourTurtleId", required = false) Integer ourTurtleId,
                              @RequestParam(name = "opponentTurtleId", required = false) Integer opponentTurtleId,
                              RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "arena");

        if(ourTurtleId == null){
            redirectAttributes.addFlashAttribute("failedMessage", "Musisz wybrać żółwia");
            return "redirect:/arena";
        }
        if(opponentTurtleId == null){
            redirectAttributes.addFlashAttribute("failedMessage", "Musisz wybrać przeciwnika");
            return "redirect:/arena";
        }

        try {
            BattleResultDTO battleResult = battleService.processFightWithOtherTurtle(user, ourTurtleId, opponentTurtleId);
            model.addAttribute("battleResult", battleResult);
            model.addAttribute("turtle", user.getTurtle(ourTurtleId));
            model.addAttribute("opponent", turtleRepository.findById(opponentTurtleId).orElseThrow());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            return "redirect:/arena";
        }

        return "pages/battlePageWithTurtle";

    }

}
