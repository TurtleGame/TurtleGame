package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.PrivateMessageAttachmentRepository;
import com.pjatk.turtlegame.repositories.TurtleStaticsRepository;
import com.pjatk.turtlegame.repositories.TurtleTrainingHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AcademyService {

    TurtleTrainingHistoryRepository turtleTrainingHistoryRepository;
    TurtleRepository turtleRepository;
    PrivateMessageAttachmentRepository privateMessageAttachmentRepository;
    private final PrivateMessageService privateMessageService;
    ItemService itemService;
    TurtleStaticsRepository turtleStaticsRepository;

    @Transactional
    public void turtleTraining (Turtle turtle, Training training, int durationTime) {
        turtle.setAvailable(false);
        turtleRepository.save(turtle);

        User user = turtle.getOwner();
        List<TrainingItem> trainingItems = training.getTrainingItemList();

        for (TrainingItem item : trainingItems) {
            itemService.removeItem(user, item.getItem().getId(), item.getHowMany() * getXPFronTraining(durationTime));
        }

        TurtleTrainingHistory turtleTraining = new TurtleTrainingHistory();
        turtleTraining.setTurtle(turtle);
        turtleTraining.setTraining(training);
        turtleTraining.setStartAt(LocalDateTime.now());
        turtleTraining.setPoints(getXPFronTraining(durationTime));
        turtleTraining.setEndAt(turtleTraining.getStartAt().plusHours(durationTime));
        turtleTrainingHistoryRepository.save(turtleTraining);
    }
    public int getXPFronTraining (int durationTime) {

        return switch (durationTime) {
            case 60 -> 1;
            case 180 -> 2;
            case 360 -> 3;
            default -> 0;
        };
    }

    @Transactional
    public void processTurtleTrainingHistory (List<TurtleTrainingHistory> turtleTrainingHistories) {
        for (TurtleTrainingHistory history : turtleTrainingHistories) {
            if (!history.isWasRewarded() && history.getEndAt().isBefore(LocalDateTime.now())) {
                List<TurtleStatistic> stats = history.getTurtle().getTurtleStatisticList();

                for (TurtleStatistic stat : stats)
                    if (stat.getStatistic().getId() == history.getTraining().getStatistic().getId()) {
                        stat.setValue(stat.getValue() + history.getPoints());
                        turtleStaticsRepository.save(stat);
                        history.setWasRewarded(true);
                    }

                turtleTrainingHistoryRepository.save(history);
                privateMessageService.sendTrainingReport(history.getTurtle());
            }
        }
    }
    
    public int getQuant (User user, Training training, int itemId) {
        List<TrainingItem> trainingItems = training.getTrainingItemList();
        List<UserItem> userItems = user.getUserItemList();

        int index = 0;

        if (itemId < 0) {
            return 0;
        }

        for(int i = 0; i < trainingItems.size(); i++) {
            if(trainingItems.get(i).getId() == itemId) {
                index = i;
            }
        }

        for (UserItem userItem : userItems) {
            if (userItem.getItem().getId() == trainingItems.get(index).getItem().getId()) {
                return userItem.getQuantity();
            }
        }

        return 0;
    }

    public boolean ifTrainingCan (User user, Training training) {
        List<TrainingItem> trainingItems = training.getTrainingItemList();
        List<UserItem> userItems = user.getUserItemList();

        int j = 0;

        for (TrainingItem trainingItem : trainingItems) {
            for (UserItem userItem : userItems) {
                if (userItem.getItem().getId() == trainingItem.getItem().getId()) {
                    if (userItem.getQuantity() < trainingItem.getHowMany())
                        return false;
                    else j++;
                }
            }
        }

        return j == trainingItems.size();
    }
}
