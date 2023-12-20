package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.BattleParticipantDTO;
import com.pjatk.turtlegame.models.DTOs.BattleResultDTO;
import com.pjatk.turtlegame.repositories.GuardsRepository;
import com.pjatk.turtlegame.repositories.TurtleBattleHistoryRepository;
import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class BattleService {

    private final GuardsRepository guardsRepository;
    private final TurtleRepository turtleRepository;
    private final TurtleBattleHistoryRepository turtleBattleHistoryRepository;
    private final ItemService itemService;
    private final UserRepository userRepository;

    public static final double NORMAL_ATTACK_MODIFIER_MIN = 0.8;

    public static final double NORMAL_ATTACK_MODIFIER_MAX = 0.9;

    public static final double NORMAL_DEFENCE_MODIFIER = 0.6;
    public static final double MAGIC_DEFENCE_MODIFIER = 0.8;

    @Transactional
    public BattleResultDTO processFightWithGuard(User user, Integer turtleId, Integer guardId) throws Exception {

        Turtle turtle = user.getTurtle(turtleId);
        Guard guard = guardsRepository.findById(guardId).orElseThrow();

        if (turtle.getEnergy() <= 0) {
            throw new Exception("Żółw ma za mało energii");
        }
        if (!turtle.isAvailable()) {
            throw new Exception("Żółw jest zajęty!");
        }

        turtle.setEnergy(turtle.getEnergy() - 2);
        turtleRepository.save(turtle);

        BattleParticipantDTO fighter1 = new BattleParticipantDTO(turtle);
        BattleParticipantDTO fighter2 = new BattleParticipantDTO(guard);

        BattleResultDTO result = fight(fighter1, fighter2);

        TurtleBattleHistory battleHistory = new TurtleBattleHistory();
        battleHistory.setCreatedAt(LocalDateTime.now());
        if (result.getWinner().equals(fighter1)) {
            battleHistory.setWinnerTurtle(turtle);
            battleHistory.setLoserGuard(guard);
        } else {
            battleHistory.setLoserTurtle(turtle);
            battleHistory.setWinnerGuard(guard);
        }
        turtleBattleHistoryRepository.save(battleHistory);
        result.setTurtleBattleHistory(battleHistory);

        if (result.getWinner().equals(fighter1)) {
            Random random = new Random();
            for (GuardItem item : guard.getGuardItemList()) {
                int randomNumber = random.nextInt(100) + 1;
                if (randomNumber <= item.getChance()) {
                    int randomQuantity = random.nextInt(item.getMaxQuantity() - item.getMinQuantity() + 1) + item.getMinQuantity();
                    itemService.addItem(user, item.getItem(), randomQuantity);
                    result.getRewards().put(item.getItem(), randomQuantity);
                }
            }

        }

        return result;
    }

    @Transactional
    public BattleResultDTO processFightWithOtherTurtle(User user, Integer ourTurtleId, Integer opponentId) throws Exception {
        Turtle ourTurtle = user.getTurtle(ourTurtleId);
        Turtle opponent = turtleRepository.findById(opponentId).orElseThrow();
        Random random = new Random();

        if (ourTurtle.getEnergy() <= 0) {
            throw new Exception("Żółw ma za mało energii");
        }
        if (!ourTurtle.isAvailable()) {
            throw new Exception("Żółw jest zajęty!");
        }

        ourTurtle.setEnergy(ourTurtle.getEnergy() - 2);


        BattleParticipantDTO fighter1 = new BattleParticipantDTO(ourTurtle);
        BattleParticipantDTO fighter2 = new BattleParticipantDTO(opponent);

        BattleResultDTO result = fight(fighter1, fighter2);

        TurtleBattleHistory battleHistory = new TurtleBattleHistory();
        battleHistory.setCreatedAt(LocalDateTime.now());
        if (result.getWinner().equals(fighter1)) {
            battleHistory.setWinnerTurtle(ourTurtle);
            battleHistory.setLoserTurtle(opponent);
        } else {
            battleHistory.setLoserTurtle(ourTurtle);
            battleHistory.setWinnerTurtle(opponent);
        }
        turtleBattleHistoryRepository.save(battleHistory);
        result.setTurtleBattleHistory(battleHistory);

        if (result.getWinner().equals(fighter1)) {

            int randomGold = random.nextInt(201) + 100;
            int randomRankingPoints = random.nextInt(11)+20;
            ourTurtle.setRankingPoints(ourTurtle.getRankingPoints() + randomRankingPoints);
            user.setGold(user.getGold() + randomGold);
            userRepository.save(user);
            result.setGold(randomGold);
            result.setGainedRankingPoints(randomRankingPoints);
        } else {
            int randomRankingPoints = random.nextInt(11)+20;
            opponent.setRankingPoints(opponent.getRankingPoints() + randomRankingPoints);
            ourTurtle.setRankingPoints(Math.max(ourTurtle.getRankingPoints() - randomRankingPoints, 0));
            result.setLostRankingPoints(randomRankingPoints);
        }
        turtleRepository.save(ourTurtle);
        turtleRepository.save(opponent);
        return result;

    }

    public Turtle findOpponent(Turtle turtle) {
        int maxDifference = 30;
        Random random = new Random();
        List<Turtle> turtleWithSameOwner = turtle.getOwner().getTurtles();

        while (maxDifference <= 1000) {
            List<Turtle> opponentsList = turtleRepository.findTurtlesByRankingPointsBetween(turtle.getRankingPoints() - maxDifference, turtle.getRankingPoints() + maxDifference);

            opponentsList.removeAll(turtleWithSameOwner);

            if (!opponentsList.isEmpty()) {

                int randomIndex = random.nextInt(opponentsList.size());
                return opponentsList.get(randomIndex);
            }

            maxDifference += 10;
        }
        return null;
    }

    public TreeMap<Turtle, Turtle> findOpponents(User user) {
        List<Turtle> turtleList = user.getTurtles();
        HashMap<Turtle, Turtle> fightPair = new HashMap<>();

        for (Turtle turtle : turtleList) {
            fightPair.put(turtle, findOpponent(turtle));
        }

        return new TreeMap<>(fightPair);
    }

    public BattleResultDTO fight(BattleParticipantDTO f1, BattleParticipantDTO f2) {
        List<StringBuilder> battleLog = new ArrayList<>();

        // current rozpoczyna, ponieważ ma więcej agility
        BattleParticipantDTO attacker = (f1.getAgility() > f2.getAgility()) ? f1 : f2;
        BattleParticipantDTO defender = (f1.getAgility() > f2.getAgility()) ? f2 : f1;

        Random random = new Random();

        String attackType;
        int round = 1;
        double defence;
        int power;

        while (true) {
            attackType = getAttackType(attacker, round);

            if (attackType.equals("normal")) {
                double modifier = NORMAL_ATTACK_MODIFIER_MIN + (random.nextDouble() * (NORMAL_ATTACK_MODIFIER_MAX - NORMAL_ATTACK_MODIFIER_MIN));
                power = (int) Math.ceil(attacker.getStrength() * modifier);
                defence = (double) defender.getStamina() / attacker.getStrength();
                defence = Math.min(defence, 1) * NORMAL_DEFENCE_MODIFIER;
                power = (int) Math.ceil(power - (power * defence));
            } else {
                power = attacker.getMp();
                defence = (double) defender.getAgility() / attacker.getMp() * MAGIC_DEFENCE_MODIFIER * 100;
                if (random.nextInt(100) + 1 <= defence) {
                    power = 0;
                }
            }
            power = Math.max(power, 0);
            defender.setCurrentHp(defender.getCurrentHp() - power);
            if (attackType.equals("magic") && power == 0) {
                battleLog.add((new StringBuilder())
                        .append(attacker.getName())
                        .append(" atakuje, ale ")
                        .append(defender.getName())
                        .append(" robi unik!")
                );
            } else {
                battleLog.add((new StringBuilder())
                        .append(attacker.getName())
                        .append(" uderza atakiem ")
                        .append(attackType)
                        .append(" zadając ")
                        .append(power)
                        .append(" punktów obrażeń. Przeciwnikowi zostaje ")
                        .append(defender.getCurrentHp())
                        .append(" punktów zdrowia.")
                );
            }

            if (defender.getCurrentHp() <= 0) {
                break;
            }

            round++;
            attacker = attacker.equals(f1) ? f2 : f1;
            defender = defender.equals(f1) ? f2 : f1;
        }
        battleLog.add((new StringBuilder())
                .append("Zwyciężył - ")
                .append(attacker.getName())
        );

        BattleResultDTO result = new BattleResultDTO();
        result.setBattleLog(battleLog);
        result.setWinner(attacker);
        result.setLoser(defender);

        return result;
    }

    private String getAttackType(BattleParticipantDTO fighter, int round) {
        if (round == 1 || round == 2) {
            if ((fighter.getStrength() + fighter.getStamina()) > (fighter.getAgility() + fighter.getMp())) {
                return "normal";
            }

            return "magic";
        }

        return (new Random()).nextBoolean() ? "magic" : "normal";
    }
}
