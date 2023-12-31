package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.config.TurtleUserDetails;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("context", "private-messages");

        return "pages/privateMessage";
    }

    @PostMapping("/{id}/delete")
    public String deleteMessage(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                @PathVariable int id) {
        User user = userRepository.findById(turtleUserDetails.getId());
        privateMessageService.deleteMessage(user, id);

        return "redirect:/private-message";
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails, @PathVariable int messageId) {
        privateMessageService.markMessageAsRead(userRepository.findById(turtleUserDetails.getId()), messageId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllMessagesAsRead(@AuthenticationPrincipal TurtleUserDetails turtleUserDetails){
        privateMessageService.markAllMessagesAsRead(userRepository.findById(turtleUserDetails.getId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public String createNewMessage(@ModelAttribute("newMessageDTO") NewMessageDTO newMessageDTO,
                                   @AuthenticationPrincipal TurtleUserDetails turtleUserDetails,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes
    ) {
        User user = userRepository.findById(turtleUserDetails.getId());
        model.addAttribute("messages", user.getRecipientPrivateMessageList());
        model.addAttribute("sentMessages", user.getSendPrivateMessageList());

        if (bindingResult.hasErrors()) {
            model.addAttribute("context", "private-messages");
            return "pages/privateMessage";

        }
        try {
            privateMessageService.createNewMessage(user, newMessageDTO.getRecipient(), newMessageDTO.getTitle(), newMessageDTO.getContent(), newMessageDTO.getGold(), newMessageDTO.getShells());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failedMessage", e.getMessage());
            return "redirect:/private-message";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Wysłałeś wiadomość!");

        return "redirect:/private-message";
    }
}
