package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PrivateMessageService {

    ItemService itemService;
    UserRepository userRepository;
    TurtleRepository turtleRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistory;
    PrivateMessageAttachmentRepository privateMessageAttachmentRepository;

    PrivateMessageRepository privateMessageRepository;

    @Transactional
    public void sendReport(int recipientId, int turtleId) throws Exception {

        User user = userRepository.findById(recipientId);
        Turtle turtle = turtleRepository.findById(turtleId).orElseThrow(() -> new Exception("Turtle not found"));
        TurtleExpeditionHistory expeditionHistory = turtleExpeditionHistory.findTopByTurtleIdOrderByEndAtDesc(turtleId);

        PrivateMessage report = new PrivateMessage();
        report.setTurtle(turtle);
        report.setRead(false);
        report.setRecipient(user);
        report.setSender(null);
        report.setSentAt(LocalDateTime.now());
        report.setTitle("Twój żółw " + turtle.getName() + " wrócił z wyprawy!");
        report.setGold(expeditionHistory.getGoldGained());
        report.setContent("Twój żółw " + turtle.getName() + " wrócił z wyprawy. \n Przyniósł " + expeditionHistory.getGoldGained() + " golda.");

        privateMessageRepository.save(report);

        for (PrivateMessageAttachment attachment : expeditionHistory.getPrivateMessageAttachments()) {
            attachment.setPrivateMessage(report);
            privateMessageAttachmentRepository.save(attachment);
            itemService.addItem(user, attachment.getItem(), attachment.getQuantity());
        }

    }

    public void deleteMessage(int userId, int messageId) {
        User user = userRepository.findById(userId);

        PrivateMessage userPrivateMessage = user
                .getRecipientPrivateMessageList()
                .stream()
                .filter(privateMessage -> privateMessage.getId() == messageId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć wiadomości"));

        privateMessageRepository.delete(userPrivateMessage);
    }

    public void markMessageAsRead(User user, int messageId) {
        Optional<PrivateMessage> messageOptional = user
                .getRecipientPrivateMessageList()
                .stream()
                .filter(privateMessage -> privateMessage.getId() == messageId)
                .findFirst();

        messageOptional.ifPresent(privateMessage -> {
            privateMessage.setRead(true);
            privateMessageRepository.save(privateMessage);
        });
    }
}
