package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "turtle_training_history")
@Setter
@Getter
public class TurtleTrainingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private TrainingSkill skill;

    @NotNull
    private int points;

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;

    @NotNull
    @Column(name = "wasRewarded", columnDefinition = "INT(1) DEFAULT 0")
    private boolean wasRewarded;
}
