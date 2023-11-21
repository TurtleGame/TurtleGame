package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PrivateMessageService {

    ItemService itemService;
    UserRepository userRepository;
    TurtleRepository turtleRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistory;
    TurtleTrainingHistoryRepository turtleTrainingHistory;
    PrivateMessageAttachmentRepository privateMessageAttachmentRepository;
    PrivateMessageRepository privateMessageRepository;

    @Transactional
    public void sendExpeditionReport(Turtle turtle) {
        User user = turtle.getOwner();
        TurtleExpeditionHistory expeditionHistory = turtleExpeditionHistory.findTopByTurtleIdOrderByEndAtDesc(turtle.getId());

        PrivateMessage report = new PrivateMessage();
        report.setTurtle(turtle);
        report.setRead(false);
        report.setRecipient(user);
        report.setSender(null);
        report.setSentAt(LocalDateTime.now());
        report.setTitle("Twój żółw " + turtle.getName() + " wrócił z wyprawy!");
        report.setGold(expeditionHistory.getGoldGained());
        report.setShells(expeditionHistory.getShellsGained());
        report.setContent("Twój żółw " + turtle.getName() + " wrócił z wyprawy. \n Zobacz co przyniósł!");

        privateMessageRepository.save(report);

        for (PrivateMessageAttachment attachment : expeditionHistory.getPrivateMessageAttachments()) {
            attachment.setPrivateMessage(report);
            privateMessageAttachmentRepository.save(attachment);
            itemService.addItem(user, attachment.getItem(), attachment.getQuantity());
        }
    }

    @Transactional
    public void sendTrainingReport(Turtle turtle) {
        User user = turtle.getOwner();
        TurtleTrainingHistory trainingHistory = turtleTrainingHistory.findTopByTurtleIdOrderByEndAtDesc(turtle.getId());

        String grammar;
        if (trainingHistory.getPoints() == 1) grammar = "punkt";
        else grammar = "punkty";

        PrivateMessage report = new PrivateMessage();
        report.setTurtle(turtle);
        report.setRead(false);
        report.setRecipient(user);
        report.setSender(null);
        report.setSentAt(LocalDateTime.now());
        report.setTitle("Twój żółw " + turtle.getName() + " skończył trening!");
        report.setGold(0);
        report.setContent("Twój żółw " + turtle.getName() + " skończył trening " + trainingHistory.getSkill() + ". \n Wytrenował " + trainingHistory.getPoints() + " " + grammar + ".");

        privateMessageRepository.save(report);
    }

    public void deleteMessage(User user, int messageId) {
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

    public void markAllMessagesAsRead(User user){
        List<PrivateMessage> messageList = user
                .getRecipientPrivateMessageList()
                .stream().toList();

        for(PrivateMessage message : messageList){
            message.setRead(true);
            privateMessageRepository.save(message);
        }
    }

    @Transactional
    public void createNewMessage(User user, String username, String title, String content, Integer gold, Integer shells) {

        if (gold == null) {
            gold = 0;
        }

        if(shells == null){
            shells = 0;
        }

        if (user.getGold() < gold) {
            throw new IllegalArgumentException("Posiadasz za mało złota!");
        }
        if (user.getShells() < shells) {
            throw new IllegalArgumentException("Posiadasz za mało muszelek!");
        }
        user.setGold(user.getGold() - gold);
        user.setShells(user.getShells() - shells);
        userRepository.save(user);

        User recipient = userRepository.findUserByUsername(username.trim());
        if (recipient == null) {
            throw new IllegalArgumentException("Użytkownik nie znaleziony");
        }
        recipient.setGold(recipient.getGold() + gold);
        recipient.setShells(recipient.getShells() + shells);
        userRepository.save(recipient);

        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setSender(user);
        privateMessage.setGold(gold);
        privateMessage.setShells(shells);
        privateMessage.setRecipient(recipient);
        privateMessage.setTitle(title);
        privateMessage.setContent(content);
        privateMessage.setSentAt(LocalDateTime.now());
        privateMessageRepository.save(privateMessage);
    }
}
