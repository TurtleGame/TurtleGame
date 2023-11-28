package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.SellTurtle;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemsController {
    private final UserRepository userRepository;
    private final ItemService itemService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("context", "items");
        model.addAttribute("items", user.getUserItemList());

        return "pages/itemsPage";
    }

    @PostMapping("/{id}/sell")
    public String sellItem(@ModelAttribute("sellTurtle") SellTurtle sellTurtle,
                          @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                          @PathVariable int id,
                          @RequestParam("Gold") int gold,
                          BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            return "pages/itemsPage";
        }

        itemService.sellItem(turtleUserDetails.getId(), id, gold);
        return "redirect:/items";
    }
}
