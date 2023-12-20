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
        return 5 * getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 1)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getMP() {
        return getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 2)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getAgility() {
        return getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 3)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getStrength() {
        return getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 4)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getStamina() {
        return getTurtleStatisticList()
                .stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getId() == 5)
                .mapToInt(TurtleStatistic::getValue).findFirst()
                .orElse(0);
    }

    @Override
    public int compareTo(Turtle otherTurtle) {
        return Objects.compare(this.name, otherTurtle.name, String::compareTo);
    }
}
