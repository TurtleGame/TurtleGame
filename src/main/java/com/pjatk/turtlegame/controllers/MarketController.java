package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.ItemOwnerMarket;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.MarketService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping(path = "/market")
@AllArgsConstructor
public class MarketController {
    private MarketService marketService;
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "turtle_id") String sortField,
                        @RequestParam(defaultValue = "asc") String sortDir) {
        model.addAttribute("context", "market");
        model.addAttribute("marketService", marketService);

        Sort.Direction direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        List<Turtle> turtles = marketService.getAllTurtles(sortField, direction);
        List<ItemOwnerMarket> items = marketService.getAllItems(sortField, direction);
        List<ItemOwnerMarket> eggs = marketService.getAllEggs(sortField, direction);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("turtles", turtles);
        model.addAttribute("items", items);
        model.addAttribute("eggs", eggs);

        return "pages/market";
    }

    @PostMapping("/{id}/buyTurtle")
    public String buyTurtle(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable int id,
                            RedirectAttributes redirectAttributes) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());

        try {
            marketService.buyTurtle(id, user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
        }

        return "redirect:/market";
    }

    @PostMapping("/{id}/undoTurtle")
    public String undoTurtle(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.undoTurtle(id, user);

        return "redirect:/market";
    }

    @PostMapping("/{id}/buyItem")
    public String buyItem(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable int id,
                            RedirectAttributes redirectAttributes) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());

        try {
            marketService.buyItem(id, user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
        }

        return "redirect:/market";
    }

    @PostMapping("/{id}/undoItem")
    public String undoItem(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.undoItem(id, user);

        return "redirect:/market";
    }
}
