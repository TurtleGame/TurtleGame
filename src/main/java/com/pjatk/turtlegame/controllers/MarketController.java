package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.models.Item;
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

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping(path = "/market")
@AllArgsConstructor
public class MarketController {
    private MarketService marketService;
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model,
                        @RequestParam(defaultValue = "") String sortType,
                        @RequestParam(defaultValue = "turtle_id") String sortField,
                        @RequestParam(defaultValue = "asc") String sortDir) {
        model.addAttribute("context", "market");
        model.addAttribute("marketService", marketService);

        List<Turtle> turtles = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        List<Item> eggs = new ArrayList<>();

        Sort.Direction direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        switch (sortType) {
            case "":
                turtles = marketService.getAllTurtles(sortField, direction);
                items = marketService.getAllItems(sortField, direction);
                eggs = marketService.getAllEggs(sortField, direction);
                break;
            case "Turtle":
                turtles = marketService.getAllTurtles(sortField, direction);
                break;
            case "Item":
                items = marketService.getAllItems(sortField, direction);
                eggs = marketService.getAllEggs(sortField, direction);
                break;
        }

        model.addAttribute("sortType", sortType);
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
                            @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.buyTurtle(id, user);

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
                            @PathVariable int id) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());
        marketService.buyItem(id, user);

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
