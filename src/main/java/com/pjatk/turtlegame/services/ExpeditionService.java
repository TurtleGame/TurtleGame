package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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
    private final ExpeditionRepository expeditionRepository;

    @Transactional
    public void turtleExpedition(int turtleId, int expeditionId, int durationTime, User user) throws Exception {

        Turtle turtle = user.getTurtle(turtleId);
        Expedition expedition = expeditionRepository.findById(expeditionId);

        if (turtle == null) {
            throw new Exception("Nie znaleziono żółwia");
        }

        if (!turtle.isAvailable()) {
            throw new Exception("Żółw " + turtle.getName() +  " jest zajęty");
        }

        if (expedition == null) {
            throw new Exception("Nie znaleziono wyprawy");
        }

        if (turtle.getLevel() < expedition.getMinLevel()) {
            throw new Exception("Wymagany level, aby wyruszyć na tą wyprawę to " + expedition.getMinLevel());
        }

        if (durationTime == 0) {
            throw new Exception("Musisz wybrać długość wyprawy");
        }

        turtle.setAvailable(false);
        turtleRepository.save(turtle);

        TurtleExpeditionHistory turtleExpedition = new TurtleExpeditionHistory();
        turtleExpedition.setTurtle(turtle);
        turtleExpedition.setExpedition(expedition);
        turtleExpedition.setGoldGained(getGoldFromExpedition(expedition, durationTime));
        turtleExpedition.setShellsGained(getShellsFromExpedition(expedition));
        turtleExpedition.setStartAt(LocalDateTime.now());
        turtleExpedition.setEndAt(turtleExpedition.getStartAt().plusMinutes(durationTime));
        turtleExpeditionHistoryRepository.save(turtleExpedition);

        for (PrivateMessageAttachment attachment : getItemsFromExpedition(expedition, durationTime)) {
            attachment.setTurtleExpeditionHistory(turtleExpedition);
            privateMessageAttachmentRepository.save(attachment);
        }
    }

    public void sendAllTurtlesOnExpedition(User user, int expeditionId, int durationTime) throws Exception {
        Expedition expedition = expeditionRepository.findById(expeditionId);
        for (Turtle turtle : user.getTurtles()) {
            if (turtle.isAvailable() && turtle.getLevel() >= expedition.getMinLevel()) {
                System.out.println(turtle.getName());
                turtleExpedition(turtle.getId(), expeditionId, durationTime, user);
            }
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

    public int getShellsFromExpedition(Expedition expedition) {
        int shell = 0;
        Random random = new Random();
        int randomChance = random.nextInt(100) + 1;
        if (randomChance <= expedition.getShellChance()) {
            shell += random.nextInt(expedition.getMaxShells()) + 1;

        }
        return shell;
    }

    public List<PrivateMessageAttachment> getItemsFromExpedition(Expedition expedition, int durationTime) {
        List<PrivateMessageAttachment> privateMessageAttachments = new ArrayList<>();
        Random random = new Random();

        boolean eggFound = false;
        boolean equipmentFound = false;
        boolean foodFound = true;

        for (int i = 30; i <= durationTime; i += 30) {
            for (ExpeditionItem expeditionItem : expedition.getExpeditionItemList()) {
                int randomNumber = random.nextInt(100) + 1;

                if (randomNumber <= expeditionItem.getChance()) {
                    if (expeditionItem.getItem().isEgg()) {
                        if (eggFound) {
                            continue;
                        }
                        eggFound = true;
                    }

                    if (expeditionItem.getItem().isEquipment()) {
                        if (equipmentFound) {
                            continue;
                        }
                        equipmentFound = true;
                    }
                    if (expeditionItem.getItem().isFood()) {
                        if (foodFound) {
                            continue;
                        }
                        foodFound = true;
                    }


                    int randomQuantity = random.nextInt(expeditionItem.getMaxQuantity() - expeditionItem.getMinQuantity() + 1) + expeditionItem.getMinQuantity();
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
    public void processTurtleExpeditionHistory(List<TurtleExpeditionHistory> turtleExpeditionHistoryList) {
        for (TurtleExpeditionHistory history : turtleExpeditionHistoryList) {
            if (!history.isWasRewarded() && history.getEndAt().isBefore(LocalDateTime.now())) {
                User user = history.getTurtle().getOwner();
                user.setGold(user.getGold() + history.getGoldGained());
                user.setShells(user.getShells() + history.getShellsGained());
                userRepository.save(user);
                history.setWasRewarded(true);
                turtleExpeditionHistoryRepository.save(history);
                privateMessageService.sendExpeditionReport(history);
            }
        }
    }

}
