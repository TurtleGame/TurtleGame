package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.DTOs.BattleParticipantDTO;
import org.springframework.stereotype.Service;

import java.util.Random;
@Service
public class BattleService {

    public static final double NORMAL_ATTACK_MODIFIER_MIN = 0.8;

    public static final double NORMAL_ATTACK_MODIFIER_MAX = 0.9;

    public static final double NORMAL_DEFENCE_MODIFIER = 0.6;
    public static final double MAGIC_DEFENCE_MODIFIER = 0.8;

    public StringBuilder fight(BattleParticipantDTO f1, BattleParticipantDTO f2) {
        StringBuilder battleLog = new StringBuilder();

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
            battleLog
                    .append(attacker.getName())
                    .append(" uderza atakiem ")
                    .append(attackType)
                    .append(" zadając ")
                    .append(power)
                    .append(" punktów obrażeń. Przeciwnikowi zostaje ")
                    .append(defender.getCurrentHp())
                    .append("\n");


            if (defender.getCurrentHp() <= 0) {
                break;
            }

            round++;
            attacker = attacker.equals(f1) ? f2 : f1;
            defender = defender.equals(f1) ? f2 : f1;
        }
        battleLog
                .append("Zwyciężył - ")
                .append(attacker.getName())
                .append("\n");
        return battleLog;
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
