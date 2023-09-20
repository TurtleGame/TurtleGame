package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.services.ItemService;
import com.pjatk.turtlegame.services.StatisticService;
import com.pjatk.turtlegame.services.TurtleService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping(path = "/turtles")
@AllArgsConstructor
public class TurtleController {
    private final TurtleService turtleService;
    private final ItemService itemService;
    private final StatisticService statisticService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "turtles");

        return "pages/turtlePage";
    }

    @PostMapping("/{id}/delete")
    public String abandonTurtle(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws Exception {

        turtleService.abandonTurtle(id, turtleUserDetails.getId());
        return "redirect:/turtles";
    }

    @GetMapping("/{id}/details")
    public String turtleDetail(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws UnauthorizedAccessException, TurtleNotFoundException {

        model.addAttribute("turtle", turtleService.getTurtleDetails(id, turtleUserDetails.getId()));
        model.addAttribute("foods", itemService.getFood(turtleUserDetails.getId()));
        model.addAttribute("statistics", itemService.getItemsStatistics());

        return "pages/turtleDetails";
    }


}
