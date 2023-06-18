package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @NotNull
    private int level;

    @NotNull
    private int unassignedPoints;

    @NotNull
    private int rankingPlace;

    @ManyToOne
    @JoinColumn(name = "turtle_type_id")
    private TurtleType turtleType;

    @OneToMany(mappedBy = "winner")
    private List<TurtleBattleHistory> wonBattles;

    @OneToMany(mappedBy = "loser")
    private List<TurtleBattleHistory> lostBattles;







}
