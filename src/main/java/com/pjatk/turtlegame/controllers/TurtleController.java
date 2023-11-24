package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.DTOs.FeedTurtleDTO;
import com.pjatk.turtlegame.models.DTOs.SellTurtle;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ItemService;
import com.pjatk.turtlegame.services.TurtleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/turtles")
@AllArgsConstructor
public class TurtleController {
    private final TurtleService turtleService;
    private final ItemService itemService;
    private final UserRepository userRepository;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "turtles");
        model.addAttribute("sellTurtle", new SellTurtle());

        return "pages/turtlePage";
    }

    @PostMapping("/{id}/delete")
    public String abandonTurtle(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) {
        User user = userRepository.findById(turtleUserDetails.getId());
        turtleService.abandonTurtle(id, user);

        return "redirect:/turtles";
    }

    @PostMapping("/{id}/sell")
    public String sellTurtle(@ModelAttribute("sellTurtle") SellTurtle sellTurtle,
                             @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                             @PathVariable int id,
                             @RequestParam("Gold") int gold,
                             Model model,
                             BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            return "pages/turtlePage";
        }

        turtleService.sellTurtle(turtleUserDetails.getId(), id, gold);
        return "redirect:/turtles";
    }

    @GetMapping("/{id}/details")
    public String turtleDetail(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws UnauthorizedAccessException, TurtleNotFoundException {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("turtle", turtleService.getTurtleDetails(id, user.getId()));
        model.addAttribute("foods", itemService.getFood(user));
        model.addAttribute("statistics", itemService.getItemsStatistics());
        model.addAttribute("feedTurtleDTO", new FeedTurtleDTO());

        return "pages/turtleDetails";
    }

    @PostMapping("/{id}/details")
    public String feedTurtle(@Valid @ModelAttribute("feedTurtleDTO") FeedTurtleDTO feedTurtleDTO,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                             Model model,
                             @PathVariable int id) throws UnauthorizedAccessException, TurtleNotFoundException {

        User user = userRepository.findById(turtleUserDetails.getId());
        if (!bindingResult.hasErrors()) {
            try {
                turtleService.feedTurtle(feedTurtleDTO.getFoodId(), user, feedTurtleDTO.getTurtleId());
                return "redirect:/turtles/{id}/details";
            } catch (Exception e) {
                bindingResult.rejectValue("foodId", "error.notFood", e.getMessage());
            }
        }

        model.addAttribute("turtle", turtleService.getTurtleDetails(id, user.getId()));
        model.addAttribute("foods", itemService.getFood(user));
        model.addAttribute("statistics", itemService.getItemsStatistics());

        return "pages/turtleDetails";
    }
}
