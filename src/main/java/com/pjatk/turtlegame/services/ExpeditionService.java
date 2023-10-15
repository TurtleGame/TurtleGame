package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.PrivateMessageAttachmentRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.pjatk.turtlegame.repositories.TurtleExpeditionHistoryRepository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class ExpeditionService {

    TurtleExpeditionHistoryRepository turtleExpeditionHistoryRepository;
    TurtleRepository turtleRepository;
    PrivateMessageAttachmentRepository privateMessageAttachmentRepository;
    private final UserRepository userRepository;
    private final PrivateMessageService privateMessageService;


    public void turtleExpedition(Turtle turtle, Expedition expedition, int durationTime) {

        turtle.setAvailable(false);
        turtleRepository.save(turtle);

        TurtleExpeditionHistory turtleExpedition = new TurtleExpeditionHistory();
        turtleExpedition.setTurtle(turtle);
        turtleExpedition.setExpedition(expedition);
        turtleExpedition.setGoldGained(getGoldFromExpedition(expedition, durationTime));
        turtleExpedition.setStartAt(LocalDateTime.now());
        turtleExpedition.setEndAt(turtleExpedition.getStartAt().plusSeconds(durationTime));
        turtleExpeditionHistoryRepository.save(turtleExpedition);

        for (PrivateMessageAttachment attachment : getItemsFromExpedition(expedition, durationTime)) {
            attachment.setTurtleExpeditionHistory(turtleExpedition);
            privateMessageAttachmentRepository.save(attachment);
        }
    }

    public int getGoldFromExpedition(Expedition expedition, int durationTime) {
        int gold = 0;
        Random random = new Random();
        for (int i = 30; i <= durationTime; i += 30) {
            gold += random.nextInt(expedition.getMaxGold()) + 1;
        }
        return gold;
    }

    public List<PrivateMessageAttachment> getItemsFromExpedition(Expedition expedition, int durationTime) {
        List<PrivateMessageAttachment> privateMessageAttachments = new ArrayList<>();
        Random random = new Random();

        for (int i = 30; i <= durationTime; i += 30) {
            for (ExpeditionItem expeditionItem : expedition.getExpeditionItemList()) {
                int randomNumber = random.nextInt(100) + 1;

                if (randomNumber <= expeditionItem.getChance()) {
                    int randomQuantity = random.nextInt(expeditionItem.getItem().getMaxQuantity() - expeditionItem.getItem().getMinQuantity() + 1) + expeditionItem.getItem().getMinQuantity();
                    boolean rewardFound = false;

                    for (PrivateMessageAttachment privateMessageAttachment : privateMessageAttachments) {
                        if (privateMessageAttachment.getItem().equals(expeditionItem.getItem())) {
                            privateMessageAttachment.setQuantity(privateMessageAttachment.getQuantity() + randomQuantity);
                            rewardFound = true;
                            break;
                        }
                    }

                    if (!rewardFound) {
                        PrivateMessageAttachment privateMessageAttachment1 = new PrivateMessageAttachment();
                        privateMessageAttachment1.setItem(expeditionItem.getItem());
                        privateMessageAttachment1.setQuantity(randomQuantity);
                        privateMessageAttachments.add(privateMessageAttachment1);
                    }
                }
            }
        }

        return privateMessageAttachments;
    }

    @Transactional
    public void processTurtleExpeditionHistory(List<TurtleExpeditionHistory> turtleExpeditionHistoryList, User user) {
        for (TurtleExpeditionHistory history : turtleExpeditionHistoryList) {
            if (!history.isWasRewarded() && history.getEndAt().isBefore(LocalDateTime.now())) {
                user.setGold((user.getGold() + history.getGoldGained()));
                history.setWasRewarded(true);
                userRepository.save(user);
                turtleExpeditionHistoryRepository.save(history);
                privateMessageService.sendReport(user.getId(), history.getTurtle());
            }
        }
    }

}
