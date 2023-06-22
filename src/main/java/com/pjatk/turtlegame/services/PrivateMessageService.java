package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Report;
import com.pjatk.turtlegame.models.Turtle;
import com.pjatk.turtlegame.models.TurtleExpeditionHistory;
import com.pjatk.turtlegame.repositories.ReportRepository;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PrivateMessageService {

    UserRepository userRepository;
    TurtleRepository turtleRepository;
    TurtleExpeditionHistoryRepository turtleExpeditionHistory;

    ReportRepository reportRepository;

    public Report sendReport(int recipientId, int turtleId){


        Turtle turtle = turtleRepository.findById(turtleId);
        TurtleExpeditionHistory expeditionHistory = turtleExpeditionHistory.findTopByTurtleIdOrderByEndAtDesc(turtleId);

        Report report = new Report();
        report.setTurtle(turtle);
        report.setUser(userRepository.findById(recipientId));
        report.setSentAt(LocalDateTime.now());
        report.setTitle("Twój żółw " + turtle.getName() + " wrócił z wyprawy!");
        report.setContent("Twój żółw " + turtle.getName() + " wrócił z wyprawy. \n Przyniósł " + expeditionHistory.getExpedition().getGold() + " golda.");

        return reportRepository.save(report);

    }
}
