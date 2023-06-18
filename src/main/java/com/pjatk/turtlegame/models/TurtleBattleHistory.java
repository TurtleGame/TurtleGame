package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "turtle_battle_history")
@Setter
@Getter
public class TurtleBattleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "winnder_id")
    private Turtle winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private Turtle loser;

    @NotNull
    private LocalDateTime createdAt;

}
