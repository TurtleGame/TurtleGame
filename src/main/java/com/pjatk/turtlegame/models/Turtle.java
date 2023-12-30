package com.pjatk.turtlegame.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

@Entity
@Table(name = "turtle")
@Setter
@Getter
public class Turtle implements Comparable<Turtle> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    private int gender;

    @NotNull
    private int level;

    @Column(name = "is_available", columnDefinition = "INT(1)")
    private boolean isAvailable;

    @Column(name = "is_fed", columnDefinition = "INT(0)")
    private boolean isFed;

    private int howMuchFood;

    private int energy;

    private int rankingPoints;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TurtleType turtleType;

    @OneToMany(mappedBy = "winnerTurtle")
    private List<TurtleBattleHistory> wonBattles;

    @OneToMany(mappedBy = "loserTurtle")
    private List<TurtleBattleHistory> lostBattles;

    @OneToMany(mappedBy = "turtle")
    @JsonIgnore
    private List<TurtleOwnerHistory> turtleOwnerHistoryList;

    @OneToMany(mappedBy = "turtle")
    private List<TurtleTransationHistory> turtleTransationHistoryList;

    @OneToMany(mappedBy = "turtle", fetch = FetchType.EAGER)
    private List<TurtleStatistic> turtleStatisticList;

    @OneToMany(mappedBy = "turtle")
    private List<UserItem> equipmentHistoryList;

    @OneToMany(mappedBy = "turtle", fetch = FetchType.EAGER)
    private List<TurtleExpeditionHistory> turtleExpeditionHistoryList;

    @OneToMany(mappedBy = "turtle", fetch = FetchType.EAGER)
    private List<TurtleTrainingHistory> turtleTrainingHistoryList;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public TurtleExpeditionHistory getCurrentExpedition() {
        return getTurtleExpeditionHistoryList()
                .stream()
                .filter(history -> history.getEndAt().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    public TurtleTrainingHistory getCurrentTraining() {
        return getTurtleTrainingHistoryList()
                .stream()
                .filter(history -> history.getEndAt().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }


    public LocalDateTime getAvailableAt() {
        if (getCurrentExpedition() != null) {
            if (getCurrentExpedition().getEndAt() != null)
                return getCurrentExpedition().getEndAt();
        } else if (getCurrentTraining() != null) {
            if (getCurrentTraining().getEndAt() != null)
                return getCurrentTraining().getEndAt();
        }
        return null;
    }

    public int getHP() {
        return  getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 1)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getHPtoFight(){
        return getHP() * 5;
    }

    public int getMP() {
        int mp = getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 2)
                .mapToInt(TurtleStatistic::getValue)
                .findFirst()
                .orElse(0);

        if(owner != null){

            int mpFromWand = owner.getUserItemList()
                    .stream()
                    .filter(wand -> "Różdżka".equals(wand.getItem().getSlot()) &&  wand.getTurtle() != null && wand.getTurtle().getId() == this.id)
                    .mapToInt(wand -> wand.getItem().getItemStatistic().getValue())
                    .findFirst()
                    .orElse(0);
            return mp + mpFromWand;
        }

        return mp;
    }

    public int getAgility() {
        int agility = getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 3)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);

        if (owner != null) {
            int agilityFromBoots = owner.getUserItemList()
                    .stream()
                    .filter(boots -> "Buty".equals(boots.getItem().getSlot()) && boots.getTurtle() != null && boots.getTurtle().getId() == this.id)
                    .mapToInt(boots -> boots.getItem().getItemStatistic().getValue())
                    .findFirst()
                    .orElse(0);
            return agilityFromBoots + agility;
        }


        return agility;
    }

    public int getStrength() {
        int strength = getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 4)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);

        if (owner != null) {
            int strengthFromSword = owner.getUserItemList()
                    .stream()
                    .filter(sword -> "Miecz".equals(sword.getItem().getSlot()) && sword.getTurtle() != null && sword.getTurtle().getId() == this.id)
                    .mapToInt(sword -> sword.getItem().getItemStatistic().getValue())
                    .findFirst()
                    .orElse(0);
            return strengthFromSword + strength;
        }
        return strength;

    }

    public int getStamina() {
        int stamina = getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 5)
                .mapToInt(TurtleStatistic::getValue)
                .findFirst()
                .orElse(0);

        if (owner != null) {
            int helmetStamina = owner.getUserItemList()
                    .stream()
                    .filter(helmet -> "Hełm".equals(helmet.getItem().getSlot()) && helmet.getTurtle() != null && helmet.getTurtle().getId() == this.id)
                    .mapToInt(helmet -> helmet.getItem().getItemStatistic().getValue())
                    .findFirst()
                    .orElse(0);

            return stamina + helmetStamina;
        }

        return stamina;
    }


    public int getStatistic(Integer statisticId) {
        return switch (statisticId) {
            case 1 -> getHP();
            case 2 -> getMP();
            case 3 -> getAgility();
            case 4 -> getStrength();
            case 5 -> getStamina();
            default -> throw new IllegalStateException("Unexpected value: " + statisticId);
        };
    }

    public UserItem getHelmet(){
        return owner
                .getUserItemList()
                .stream()
                .filter(helmet -> helmet.getTurtle() != null && helmet.getTurtle().getId() == this.getId() && helmet.getItem().getSlot().equals("Hełm"))
                .findFirst()
                .orElse(null);
    }

    public UserItem getSword(){
        return owner
                .getUserItemList()
                .stream()
                .filter(sword -> sword.getTurtle() != null && sword.getTurtle().getId() == this.getId() && sword.getItem().getSlot().equals("Miecz"))
                .findFirst()
                .orElse(null);
    }

    public UserItem getWand(){
        return owner
                .getUserItemList()
                .stream()
                .filter(wand -> wand.getTurtle() != null && wand.getTurtle().getId() == this.getId() && wand.getItem().getSlot().equals("Różdżka"))
                .findFirst()
                .orElse(null);
    }

    public UserItem getBoots(){
        return owner
                .getUserItemList()
                .stream()
                .filter(boots -> boots.getTurtle() != null && boots.getTurtle().getId() == this.getId() && boots.getItem().getSlot().equals("Buty"))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int compareTo(Turtle otherTurtle) {
        return Objects.compare(this.name, otherTurtle.name, String::compareTo);
    }
}
