package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.DTOs.SellTurtle;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.ItemOwnerMarketRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemsController {
    private final UserRepository userRepository;
    ItemOwnerMarketRepository itemOwnerMarketRepository;
    private final ItemService itemService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "items");
        model.addAttribute("items", user.getUserItemList());
        model.addAttribute("sellTurtle", new SellTurtle());

        return "pages/itemsPage";
    }

    @GetMapping("/{id}/details")
    public String itemDetail(Model model,
                             @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                             @PathVariable int id) throws UnauthorizedAccessException, TurtleNotFoundException {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "items");
        model.addAttribute("item", itemService.getItemDetails(id, user.getId()));
        model.addAttribute("statistics", itemService.getItemsStatistics());

        return "pages/itemDetails";
    }

    @PostMapping("/{id}/details")
    public String sellItem(Model model,
                           @ModelAttribute("sellTurtle") SellTurtle sellTurtle,
                          @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                          @PathVariable int id,
                          @RequestParam("Gold") int gold,
                          @RequestParam("Quantity") int quantity,
                          BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {

        User user = userRepository.findById(turtleUserDetails.getId());

        if (bindingResult.hasErrors()) {
            return "pages/itemDetails";
        }

        if (!itemOwnerMarketRepository.existsByItemIdAndUserId(id, user.getId())) {
            try {
                itemService.sellItem(user.getId(), id, gold, quantity);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            }
        }
        else {
            redirectAttributes.addFlashAttribute("failedMessage", "Już sprzedajesz ten przedmiot!");
            model.addAttribute("context", "items");
            model.addAttribute("item", itemService.getItemDetails(id, user.getId()));
            model.addAttribute("statistics", itemService.getItemsStatistics());

            return "redirect:/items/{id}/details";
        }



        if (itemService.getItem(id, user.getId())) {

            model.addAttribute("context", "items");
            model.addAttribute("item", itemService.getItemDetails(id, user.getId()));
            model.addAttribute("statistics", itemService.getItemsStatistics());

            return "redirect:/items/{id}/details";
        }

        return "redirect:/items";

    }
}
