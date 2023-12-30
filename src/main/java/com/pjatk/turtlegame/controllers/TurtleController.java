package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.DTOs.FeedTurtleDTO;
import com.pjatk.turtlegame.models.DTOs.SellTurtle;
import com.pjatk.turtlegame.models.Turtle;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
                             @RequestParam("Gold") int shells,
                             Model model,
                             BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            return "pages/turtlePage";
        }

        turtleService.sellTurtle(turtleUserDetails.getId(), id, shells);
        return "redirect:/turtles";
    }

    @GetMapping("/{id}/details")
    public String turtleDetail(Model model,
                               @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                               @PathVariable int id,
                               RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            model.addAttribute("turtle", turtleService.getTurtleDetails(id, user.getId()));
            model.addAttribute("armor", user.getUserItemList());
            model.addAttribute("foods", itemService.getFood(user));
            model.addAttribute("statistics", itemService.getItemsStatistics());
            addEquipmentsModels(model, id, user);
            model.addAttribute("feedTurtleDTO", new FeedTurtleDTO());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            return "pages/turtlePage";
        }


        return "pages/turtleDetails";
    }

    private void addEquipmentsModels(Model model, @PathVariable int id, User user) {
        model.addAttribute("helmets", itemService.getHelmets(user, user.getTurtle(id)));
        model.addAttribute("wands", itemService.getWands(user, user.getTurtle(id)));
        model.addAttribute("swords", itemService.getSwords(user, user.getTurtle(id)));
        model.addAttribute("boots", itemService.getBoots(user, user.getTurtle(id)));
    }

    @GetMapping("/{id}/fight-history")
    public String turtleFightHistory(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int id) {
        User user = userRepository.findById(turtleUserDetails.getId());
        Turtle turtle = user.getTurtle(id);
        model.addAttribute("turtle", turtle);
        model.addAttribute("historyWithTurtles", turtleService.findTurtleBattleHistoryWithOtherTurtle(turtle));


        return "pages/historyPage";
    }

    @PostMapping("/{id}/details")
    public String feedTurtle(@Valid @ModelAttribute("feedTurtleDTO") FeedTurtleDTO feedTurtleDTO,
                             @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                             Model model,
                             @PathVariable int id,
                             RedirectAttributes redirectAttributes) throws UnauthorizedAccessException, TurtleNotFoundException {

        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            turtleService.feedTurtle(feedTurtleDTO.getFoodId(), user, feedTurtleDTO.getTurtleId());
            return "redirect:/turtles/{id}/details";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("successMessage", "Założyłeś ekwipunek!");
        }


        addModels(model, id, user);

        return "pages/turtleDetails";
    }

    @PostMapping("/{id}/details/wear")
    public String wearArmor(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                            Model model,
                            @RequestParam(name = "helmetId", required = false) Integer helmetId,
                            @RequestParam(name = "wandId", required = false) Integer wandId,
                            @RequestParam(name = "weaponId", required = false) Integer weaponId,
                            @RequestParam(name = "bootsId", required = false) Integer bootsId,
                            @PathVariable int id,
                            RedirectAttributes redirectAttributes
    ) throws UnauthorizedAccessException, TurtleNotFoundException {
        User user = userRepository.findById(turtleUserDetails.getId());

        try {
            itemService.wearItems(user, helmetId, weaponId, wandId, bootsId, id);

            redirectAttributes.addFlashAttribute("successMessage", "Założyłeś ekwipunek!");
            return "redirect:/turtles/{id}/details";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            addModels(model, id, user);

            return "redirect:/turtles/{id}/details";
        }


    }

    private void addModels(Model model, @PathVariable int id, User user) throws TurtleNotFoundException, UnauthorizedAccessException {
        model.addAttribute("turtle", turtleService.getTurtleDetails(id, user.getId()));
        model.addAttribute("foods", itemService.getFood(user));
        addEquipmentsModels(model, id, user);
        model.addAttribute("statistics", itemService.getItemsStatistics());
    }
}
