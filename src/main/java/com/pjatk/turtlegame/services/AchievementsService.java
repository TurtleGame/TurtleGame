package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementsService {
    PrivateMessageRepository privateMessageRepository;
    AchievementsEarnedRepoistory achievementsEarnedRepoistory;
    AchievementsRepoistory achievementsRepoistory;
    UserService userService;

    public void checkAchievements(User user) {
        friendss(user);
        billionaire(user);
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
    public void beginnings() {

    }

    //wyhoduj 20 żółwi (Duża rodzina)
    public void bigFamily() {

    }

    //zdobądź unikalnego żółwia (Unikalny)
    public void unique() {

    }

    //wyhoduj Ducha (Legenda)
    public void legend() {

    }

    //zdobądź żółwia z każdej kategorii (Elementals)
    public void Elementals() {

    }

    //żółw osiąga 5 poziom (Dumny hodowca)
    public void ProudBreeder () {

    }

    //żółw osiąga 20 poziom (Tak szybko dorastają)
    public void theyGrowUpSoFast() {

    }

    //nakarm żółwia 20 razy (Foodie)
    public void Foodie() {

    }

    //wyposaż żółwia każdym elementem ubioru (?) (Fashionista)
    public void fashionista() {

    }

    //wyposaż żółwia w rzadkie lub legendarne przedmioty (?) (O wow)
    public void ohMy() {

    }

    //sprzedaj żółwia na rynku (Pogódź się z tym)
    public void moveOn() {

    }

    //wyrusz na wyprawę (Pora na przygodę)
    public void adventureTime(User user) {
        Achievement achievement = achievementsRepoistory.findById(12);
    }

    //wyrusz na 100 wypraw (Poszukiwacz przygód)
    public void adventurer() {

    }

    //pokonaj 1 żółwia na arenie (Zwycięstwo)
    public void victory() {

    }

    //pokonaj 20 żółwi na arenie (Wojownik)
    public void fighterr() {

    }

    //pokonaj Ducha (Champion)
    public void champion() {

    }

    //miej 5 znajomych (Przyjaciele)
    public void friendss(User user) {
        Achievement achievement = achievementsRepoistory.findById(17);
        if (isNotEarned(user, achievement)) {
            if (userService.getFriends(user).size() >= 5) {
                addAchievement(user, achievement);
            }
        }
    }

    //Zarób 1000 muszelek (Miliarder)
    public void billionaire(User user) {
        Achievement achievement = achievementsRepoistory.findById(18);
        if (isNotEarned(user, achievement)) {
            if (user.getShells() >= 1000) {
                addAchievement(user, achievement);
            }
        }
    }
}
