package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.BattleParticipantDTO;
import com.pjatk.turtlegame.models.Guard;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.GuardsRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.BattleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/guards")
@AllArgsConstructor
public class GuardsController {
    private final UserRepository userRepository;
    private final GuardsRepository guardsRepository;
    private final BattleService battleService;
    private final TurtleRepository turtleRepository;

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
                              @RequestParam("guardId") Integer guardId,
                              @RequestParam("turtleId") Integer turtleId,
                              HttpServletRequest request) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "guards");
        Turtle turtle = user.getTurtle(turtleId);
        Guard guard = guardsRepository.findById(guardId).orElseThrow();
        turtle.setEnergy(turtle.getEnergy() - 2);
        turtleRepository.save(turtle);

        BattleParticipantDTO fighter1 = new BattleParticipantDTO(user.getTurtle(turtleId));
        BattleParticipantDTO fighter2 = new BattleParticipantDTO(guard);

        String battleUUID = UUID.randomUUID().toString();

        HttpSession session = request.getSession();
        session.setAttribute("fighter1", fighter1);
        session.setAttribute("fighter2", fighter2);
        session.setAttribute("battleUUID", battleUUID);

        return "redirect:/guards/" + battleUUID;

    }

    @GetMapping("/{battleUUID}")
    public String handleBattle(@PathVariable String battleUUID,
                               Model model,
                               @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                               HttpSession session) {



        BattleParticipantDTO fighter1 = (BattleParticipantDTO) session.getAttribute("fighter1");
        BattleParticipantDTO fighter2 = (BattleParticipantDTO) session.getAttribute("fighter2");


        model.addAttribute("fighter1", fighter1);
        model.addAttribute("fighter2", fighter2);
        model.addAttribute("fight", battleService.fight(fighter1, fighter2));

        return "pages/battlePage";
    }
}
