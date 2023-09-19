package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.services.ItemService;
import com.pjatk.turtlegame.services.TurtleService;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping(path = "/turtles")
@AllArgsConstructor
public class TurtleController {
    private final TurtleService turtleService;
    private final ItemService itemService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "turtles");

        return "pages/turtlePage";
    }

    @PostMapping("/{id}/delete")
    public String abandonTurtle(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws Exception {

        turtleService.abandonTurtle(id, turtleUserDetails.getId());
        return "pages/main";
    }

    @GetMapping("/{id}/details")
    public String turtleDetail(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws UnauthorizedAccessException, TurtleNotFoundException {

        model.addAttribute("turtle", turtleService.getTurtleDetails(id, turtleUserDetails.getId()));
        model.addAttribute("foods", itemService.getFood(turtleUserDetails.getId()));

        return "pages/turtleDetails";
    }


}
