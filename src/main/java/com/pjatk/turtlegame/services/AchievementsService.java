package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class AchievementsService {
   private final PrivateMessageRepository privateMessageRepository;
   private final AchievementsEarnedRepoistory achievementsEarnedRepoistory;
   private final TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
   private final AchievementsRepoistory achievementsRepoistory;
   private final UserService userService;

    @Transactional
    public void checkAchievements(User user) throws Exception {
        try{
            beginnings(user);
            bigFamily(user);
            unique(user);
            legend(user);
            elementals(user);
            proudBreeder(user);
            theyGrowUpSoFast(user);
            foodie(user);
            fashionista(user);
            ohMy(user);
            moveOn(user);
            adventureTime(user);
            adventurer(user);
            victory(user);
            fighters(user);
            champion(user);
            friend(user);
            billionaire(user);
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public boolean isNotEarned(User user, Achievement achievement) {
        Optional<AchievementsEarned> earned = achievementsEarnedRepoistory.findByUserAndAchievement(user, achievement);

        return earned.isEmpty();
    }

    @Transactional
    public void addAchievement(User user, Achievement achievement) {
        AchievementsEarned earn = new AchievementsEarned();
        PrivateMessage privateMessage = new PrivateMessage();
        earn.setAchievedAt(LocalDateTime.now());
        earn.setAchievement(achievement);
        earn.setUser(user);

        privateMessage.setTitle("Osiagnięcie zostało zdobyte!");
        privateMessage.setContent("Osiągnięcie " + achievement.getName() + " zostało przez ciebie zdobyte.");
        privateMessage.setRecipient(user);
        privateMessage.setSentAt(LocalDateTime.now());
        privateMessage.setGold(0);
        privateMessageRepository.save(privateMessage);
        achievementsEarnedRepoistory.save(earn);
    }

    //wyhoduj 1 żółwia (Początki)
    @Transactional
    public void beginnings(User user) {
        Achievement achievement = achievementsRepoistory.findById(1);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()) != null) {
                    if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()).getFirstOwner() == 1) {
                        addAchievement(user, achievement);
                        return;
                    }
                }
            }
        }
    }

    //wyhoduj 20 żółwi (Duża rodzina)
    @Transactional
    public void bigFamily(User user) {
        Achievement achievement = achievementsRepoistory.findById(2);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();
            int size = 0;

            for (Turtle turtle : turtles) {
                if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()) != null) {
                    if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()).getFirstOwner() == 1) {
                        size += 1;
                    }
                }
            }

            if (size >= 20) {
                addAchievement(user, achievement);
            }
        }
    }

    //zdobądź unikalnego żółwia (Unikalny)
    @Transactional
    public void unique(User user) {
        Achievement achievement = achievementsRepoistory.findById(3);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtle.getTurtleType().getRarity() != null) {
                    if (turtle.getTurtleType().getRarity().getId() == 2) { //unikalny/rzadki żółw ma id 2
                        addAchievement(user, achievement);
                        return;
                    }
                }
            }
        }
    }

    //wyhoduj Ducha (Legenda)
    @Transactional
    public void legend(User user) {
        Achievement achievement = achievementsRepoistory.findById(4);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtle.getTurtleType().getRarity() != null) {
                    if (turtle.getTurtleType().getRarity().getId() == 3) { //Duch jest jedynym legendarnym żółwiem - legendarny żółw ma id 3
                        addAchievement(user, achievement);
                        return;
                    }
                }
            }
        }
    }

    //zdobądź żółwia z każdej kategorii (Elementals)
    @Transactional
    public void elementals(User user) {
        Achievement achievement = achievementsRepoistory.findById(5);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();
            List<TurtleType> types = new ArrayList<>();

            for (Turtle turtle : turtles) {
                types.add(turtle.getTurtleType());
            }
            Set<TurtleType> filteredTypes = new HashSet<>(types);

            if (!filteredTypes.isEmpty()) {
                if (filteredTypes.size() == 7)
                    addAchievement(user, achievement);
            }
        }
    }

    //żółw osiąga 5 poziom (Dumny hodowca)
    @Transactional
    public void proudBreeder(User user) {
        Achievement achievement = achievementsRepoistory.findById(6);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()) != null) {
                    if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()).getFirstOwner() == 1) {
                        if (turtle.getLevel() >= 5) {
                            addAchievement(user, achievement);
                            return;
                        }
                    }
                }
            }
        }
    }

    //żółw osiąga 20 poziom (Tak szybko dorastają)
    @Transactional
    public void theyGrowUpSoFast(User user) {
        Achievement achievement = achievementsRepoistory.findById(7);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()) != null) {
                    if (turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtle.getId()).getFirstOwner() == 1) {
                        if (turtle.getLevel() >= 20) {
                            addAchievement(user, achievement);
                            return;
                        }
                    }
                }
            }
        }
    }

    //nakarm żółwia 20 razy (Foodie)
    @Transactional
    public void foodie(User user) {
        Achievement achievement = achievementsRepoistory.findById(8);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtle.getHowMuchFood() >= 20) {
                    addAchievement(user, achievement);
                    return;
                }
            }
        }
    }

    //wyposaż żółwia każdym elementem ubioru (Fashionista)
    @Transactional
    public void fashionista(User user) {
        Achievement achievement = achievementsRepoistory.findById(9);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtle.getHelmet() != null && turtle.getWand() != null && turtle.getSword() != null && turtle.getBoots() != null) {
                    addAchievement(user, achievement);
                    return;
                }

            }
        }
    }


    //wyposaż żółwia w rzadkie lub legendarne przedmioty (O wow)
    @Transactional
    public void ohMy(User user) {
        Achievement achievement = achievementsRepoistory.findById(10);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();
            List<UserItem> userItems = user.getUserItemList().stream().toList();
            List<Item> items = new ArrayList<>();

            int is = 0;

            for (Turtle turtle : turtles) {
                for (UserItem item : userItems) {
                    if (item.getTurtle() == turtle) {
                        items.add(item.getItem());
                    }
                    
                }

                if (items.size() == 4) {
                    for (Item item : items) {
                        if (item.getRarity().getId() >= 2) {
                            is += 1;
                        }
                    }
                    if (is == 4) {
                        addAchievement(user, achievement);
                        return;
                    }
                }
            }
        }
    }

    //sprzedaj żółwia na rynku (Pogódź się z tym)
    @Transactional
    public void moveOn(User user) {
        Achievement achievement = achievementsRepoistory.findById(11);
        if (isNotEarned(user, achievement)) {
            List<TurtleOwnerHistory> sold = user.getOwnerHistoryList().stream()
                    .filter(history -> history.getEndAt() != null && history.isSelling())
                    .toList();

            if (!sold.isEmpty())
                addAchievement(user, achievement);
        }
    }

    //wyrusz na wyprawę (Pora na przygodę)
    @Transactional
    public void adventureTime(User user) {
        Achievement achievement = achievementsRepoistory.findById(12);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (!turtle.getTurtleExpeditionHistoryList().isEmpty())
                    addAchievement(user, achievement);
                return;
            }
        }

    }

    //wyrusz na 100 wypraw (Poszukiwacz przygód)
    @Transactional
    public void adventurer(User user) {
        Achievement achievement = achievementsRepoistory.findById(13);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();
            int expeditions = 0;

            for (Turtle turtle : turtles) {
                for (TurtleExpeditionHistory ignored : turtle.getTurtleExpeditionHistoryList()) {
                    expeditions += 1;
                }
            }

            if (expeditions >= 100) {
                addAchievement(user, achievement);
            }
        }
    }

    //pokonaj 1 żółwia na arenie (Zwycięstwo)
    @Transactional
    public void victory(User user) {
        Achievement achievement = achievementsRepoistory.findById(14);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtle.getWonBattles() != null) {
                    for (TurtleBattleHistory history : turtle.getWonBattles()) {
                        if (history.getLoserTurtle() != null) {
                            addAchievement(user, achievement);
                            return;
                        }
                    }
                }
            }
        }
    }

    //pokonaj 20 żółwi na arenie (Wojownik)
    @Transactional
    public void fighters(User user) {
        Achievement achievement = achievementsRepoistory.findById(15);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();
            int battles = 0;

            for (Turtle turtle : turtles) {
                if (turtle.getWonBattles() != null) {
                    for (TurtleBattleHistory history : turtle.getWonBattles().toArray(new TurtleBattleHistory[0])) {
                        if (history.getLoserTurtle() != null) {
                            battles += 1;
                        }
                    }
                }
            }

            if (battles >= 20) {
                addAchievement(user, achievement);
            }
        }
    }

    //pokonaj Ducha (Champion)
    @Transactional
    public void champion(User user) {
        Achievement achievement = achievementsRepoistory.findById(16);
        if (isNotEarned(user, achievement)) {
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                if (turtle.getWonBattles() != null) {
                    for (TurtleBattleHistory history : turtle.getWonBattles().toArray(new TurtleBattleHistory[0])) {
                        if (history.getLoserGuard() != null && history.getLoserGuard().getId() == 3) { //Duch jako guardian ma id 3
                            addAchievement(user, achievement);
                            return;
                        }
                    }
                }
            }
        }
    }

    //miej 5 znajomych (Przyjaciele)
    @Transactional
    public void friend(User user) {
        Achievement achievement = achievementsRepoistory.findById(17);
        if (isNotEarned(user, achievement)) {
            if (userService.getFriends(user).size() >= 5) {
                addAchievement(user, achievement);
            }
        }
    }

    //Zarób 1000 muszelek (Miliarder)
    @Transactional
    public void billionaire(User user) {
        Achievement achievement = achievementsRepoistory.findById(18);
        if (isNotEarned(user, achievement)) {
            int shells = 0;
            List<TurtleOwnerHistory> sold = user.getOwnerHistoryList().stream()
                    .filter(history -> history.getEndAt() != null)
                    .toList();
            List<Turtle> turtles = user.getTurtles().stream().toList();

            for (Turtle turtle : turtles) {
                for (TurtleExpeditionHistory history : turtle.getTurtleExpeditionHistoryList()) {
                    shells = shells + history.getShellsGained();
                }
            }

            for (TurtleOwnerHistory turtle : sold) {
                shells = shells + turtle.getHowMuch();
            }

            if (shells >= 1000) {
                addAchievement(user, achievement);
            }
        }
    }
}
