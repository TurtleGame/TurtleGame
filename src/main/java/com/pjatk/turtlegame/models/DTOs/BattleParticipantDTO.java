package com.pjatk.turtlegame.models.DTOs;

import com.pjatk.turtlegame.models.Guard;
import com.pjatk.turtlegame.models.Turtle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BattleParticipantDTO {

    private String name;

    private int hp;

    private int currentHp;

    private int strength;

    private int agility;

    private int mp;

    private int stamina;


    public BattleParticipantDTO(Turtle turtle) {
        this.name = turtle.getName();
        this.hp = turtle.getHP();
        this.currentHp = turtle.getHP();
        this.strength = turtle.getStrength();
        this.agility = turtle.getAgility();
        this.mp = turtle.getMP();
        this.stamina = turtle.getStamina();
    }

    public BattleParticipantDTO(Guard guard) {
        this.name = guard.getName();
        this.hp = guard.getHP();
        this.currentHp = guard.getHP();
        this.strength = guard.getStrength();
        this.agility = guard.getAgility();
        this.mp = guard.getMP();
        this.stamina = guard.getStamina();
    }
}
