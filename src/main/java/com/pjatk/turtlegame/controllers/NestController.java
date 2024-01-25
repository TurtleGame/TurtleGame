package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.EggsForm;
import com.pjatk.turtlegame.models.DTOs.SellTurtle;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.ItemOwnerMarketRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.ItemService;
import com.pjatk.turtlegame.services.TurtleEggService;
import com.pjatk.turtlegame.services.TurtleService;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/nest")
@AllArgsConstructor
public class NestController {
    UserRepository userRepository;
    ItemOwnerMarketRepository itemOwnerMarketRepository;
    UserService userService;
    TurtleService turtleService;
    ItemService itemService;
    TurtleEggService turtleEggService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        model.addAttribute("context", "nest");
        model.addAttribute("eggs", itemService.getEggs(turtleUserDetails.getId()));
        model.addAttribute("eggsForm", new EggsForm());
        model.addAttribute("sellTurtle", new SellTurtle());

        return "pages/nest";
    }

    @PostMapping("/{id}/sell")
    public String sellEgg(@ModelAttribute("sellTurtle") SellTurtle sellTurtle,
                          @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                          @PathVariable int id,
                          @RequestParam("Gold") int gold,
                          @RequestParam("Quantity") int quantity,
                           Model model,
                           RedirectAttributes redirectAttributes){


        if (!itemOwnerMarketRepository.existsByItemIdAndUserId(id, turtleUserDetails.getId())) {
            try {
                itemService.sellEgg(turtleUserDetails.getId(), id, gold, quantity);
            } catch (Exception e) {
                model.addAttribute("eggs", itemService.getEggs(turtleUserDetails.getId()));
                redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            }
        } else {
            model.addAttribute("eggs", itemService.getEggs(turtleUserDetails.getId()));
            redirectAttributes.addFlashAttribute("failedMessage", "Już sprzedajesz to jajko!");

            return "pages/nest";
        }

        return "redirect:/nest";
    }

    @PostMapping("/{id}/adopt")
    public String adoptEgg(@ModelAttribute("eggsForm") EggsForm eggsForm,
                           @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                           @PathVariable int id,
                           @RequestParam("Name") String name,
                           @RequestParam("Gender") int gender,
                           Model model){
        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            itemService.adoptEgg(user, id, name, gender);
        } catch (Exception e) {
            model.addAttribute("eggs", itemService.getEggs(turtleUserDetails.getId()));
            model.addAttribute("failedMessage", e.getMessage());
            return "pages/nest";
        }

        return "redirect:/nest";
    }

    @PostMapping("/{id}/warm")
    public String warmEgg(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) throws Exception {
        turtleEggService.warmEgg(turtleUserDetails.getId(), id);
        return "redirect:/nest";
    }
}