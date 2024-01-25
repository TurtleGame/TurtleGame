package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import jakarta.persistence.EntityManager;
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
    EntityManager entityManager;

    @org.springframework.transaction.annotation.Transactional
    public void sendExpeditionReport(TurtleExpeditionHistory expeditionHistory) {
        Turtle turtle = expeditionHistory.getTurtle();
        User user = turtle.getOwner();

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
        List<PrivateMessageAttachment> privateMessageAttachmentList = privateMessageAttachmentRepository.findAllByTurtleExpeditionHistoryId(expeditionHistory.getId());
        for (PrivateMessageAttachment attachment : privateMessageAttachmentList) {
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
        report.setContent("Twój żółw " + turtle.getName() + " skończył trening " + trainingHistory.getTraining().getName() + ". \n Wytrenował " + trainingHistory.getPoints() + " " + grammar + ".");

        privateMessageRepository.save(report);
    }

    @Transactional
    public void sendWelcomeMessage(User user) {
        PrivateMessage welcomeMessage = new PrivateMessage();
        welcomeMessage.setRead(false);
        welcomeMessage.setRecipient(user);
        welcomeMessage.setSender(null);
        welcomeMessage.setSentAt(LocalDateTime.now());
        welcomeMessage.setTitle("Witaj w TurtleBlast!");
        welcomeMessage.setContent("Witaj w TurtleBlast! \n W zakładce Legowisko czeka na Ciebie niespodzianka. \n Powodzenia w grze, Administracja TurtleBlast");
        privateMessageRepository.save(welcomeMessage);
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

    public void markAllMessagesAsRead(User user) {
        List<PrivateMessage> messageList = user
                .getRecipientPrivateMessageList()
                .stream().toList();

        for (PrivateMessage message : messageList) {
            message.setRead(true);
            privateMessageRepository.save(message);
        }
    }

    @Transactional
    public void createNewMessage(User user, String username, String title, String content, Integer gold, Integer shells) {

        if (gold == null) {
            gold = 0;
        }

        if (shells == null) {
            shells = 0;
        }
        if (user.getUsername().equals(username)) {
            throw new IllegalArgumentException("Nie możesz wysłać wiadomości do siebie!");
        }
        if (user.getGold() < gold) {
            throw new IllegalArgumentException("Posiadasz za mało złota!");
        }
        if (user.getShells() < shells) {
            throw new IllegalArgumentException("Posiadasz za mało muszelek!");
        }
        if (gold < 0) {
            throw new IllegalArgumentException("Błędna wartość!");
        }
        if (shells < 0) {
            throw new IllegalArgumentException("Błędna wartość!");
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
