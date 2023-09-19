package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.PrivateMessage;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleExpeditionHistory;
import com.pjatk.turtlegame.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PrivateMessageService {

    UserRepository userRepository;
    TurtleRepository turtleRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistory;

    PrivateMessageRepository privateMessageRepository;

    public void sendReport(int recipientId, int turtleId) throws Exception {


        Turtle turtle = turtleRepository.findById(turtleId).orElseThrow(() ->new Exception("Turtle not found"));
        TurtleExpeditionHistory expeditionHistory = turtleExpeditionHistory.findTopByTurtleIdOrderByEndAtDesc(turtleId);

        PrivateMessage report = new PrivateMessage();
        report.setTurtle(turtle);
        report.setRecipient(userRepository.findById(recipientId));
        report.setSender(userRepository.findById(1));
        report.setSentAt(LocalDateTime.now());
        report.setTitle("Twój żółw " + turtle.getName() + " wrócił z wyprawy!");
        report.setContent("Twój żółw " + turtle.getName() + " wrócił z wyprawy. \n Przyniósł " + expeditionHistory.getExpedition().getGold() + " golda.");

        privateMessageRepository.save(report);

    }
}
