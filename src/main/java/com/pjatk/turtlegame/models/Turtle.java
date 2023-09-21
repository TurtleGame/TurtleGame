package com.pjatk.turtlegame.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalInt;

@Entity
@Table(name = "turtle")
@Setter
@Getter
public class Turtle {
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

    private int unassignedPoints;

    @Column(name = "is_available", columnDefinition = "INT(1)")
    private boolean isAvailable;

    @Column(name = "is_fed", columnDefinition = "INT(0)")
    private boolean isFed;

    private int energy;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TurtleType turtleType;

    @OneToMany(mappedBy = "winner")
    private List<TurtleBattleHistory> wonBattles;

    @OneToMany(mappedBy = "loser")
    private List<TurtleBattleHistory> lostBattles;

    @OneToMany(mappedBy = "turtle")
    private List<TurtleTrainingHistory> turtleTrainingHistoryList;

    @OneToMany(mappedBy = "turtle")
    @JsonIgnore
    private List<TurtleOwnerHistory> turtleOwnerHistoryList;

    @OneToMany(mappedBy = "turtle")
    private List<TurtleTransationHistory> turtleTransationHistoryList;

    @OneToMany(mappedBy = "turtle")
    private List<TurtleStatistic> turtleStatisticList;

    @OneToMany(mappedBy = "turtle")
    private List<UserItem> equipmentHistoryList;

    @OneToMany(mappedBy = "turtle")
    private List<TurtleExpeditionHistory> turtleExpeditionHistoryList;

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

    public int getHP() {
        OptionalInt hpValue = turtleStatisticList.stream()
                .filter(turtleStatistic -> turtleStatistic.getStatistic().getName().equals("HP"))
                .mapToInt(turtleStatistic -> turtleStatistic.getValue() * 4)
                .findFirst();

        return hpValue.orElse(0);
    }

}
