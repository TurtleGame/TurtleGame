package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
import com.pjatk.turtlegame.models.DTOs.FeedTurtleDTO;
import com.pjatk.turtlegame.models.DTOs.NewMessageDTO;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.repositories.UserRepository;
import com.pjatk.turtlegame.services.PrivateMessageService;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/private-message")
@AllArgsConstructor
public class PrivateMessageController {

    UserRepository userRepository;
    UserService userService;
    PrivateMessageService privateMessageService;

    @GetMapping(path = "")
    public String index(Model model, @AuthenticationPrincipal TurtleUserDetails turtleUserDetails) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("messages", user.getRecipientPrivateMessageList());
        model.addAttribute("sentMessages", user.getSendPrivateMessageList());
        model.addAttribute("newMessageDTO", new NewMessageDTO());
        return "pages/privateMessage";
    }

    @PostMapping("/{id}/delete")
    public String deleteMessage(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                @PathVariable int id) {

        privateMessageService.deleteMessage(turtleUserDetails.getId(), id);
        return "redirect:/private-message";
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int messageId) {
        privateMessageService.markMessageAsRead(userRepository.findById(turtleUserDetails.getId()), messageId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public String createNewMessage(@ModelAttribute("newMessageDTO") NewMessageDTO newMessageDTO,
                                   @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                   BindingResult bindingResult,
                                   Model model
    ) {
        if (!bindingResult.hasErrors()) {
            try {
                privateMessageService.createNewMessage(turtleUserDetails.getUser(), newMessageDTO.getRecipient(), newMessageDTO.getTitle(), newMessageDTO.getContent(), newMessageDTO.getGold());
            } catch (Exception e) {
                bindingResult.rejectValue("recipient", "error.notFound", e.getMessage());
            }
        }

        if (bindingResult.hasErrors()) {
            User user = userRepository.findById(turtleUserDetails.getId());
            model.addAttribute("messages", user.getRecipientPrivateMessageList());
            model.addAttribute("sentMessages", user.getSendPrivateMessageList());

            return "pages/privateMessage";
        }
        return "redirect:/private-message";
    }
}
