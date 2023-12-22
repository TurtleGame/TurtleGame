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
    @JoinColumn(name = "winner_turtle_id")
    private Turtle winnerTurtle;

    @ManyToOne
    @JoinColumn(name = "loser_turtle_id")
    private Turtle loserTurtle;

    @ManyToOne
    @JoinColumn(name = "winner_guard_id")
    private Guard winnerGuard;

    @ManyToOne
    @JoinColumn(name = "loser_guard_id")
    private Guard loserGuard;

    @NotNull
    private LocalDateTime createdAt;

}
