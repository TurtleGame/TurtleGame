package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

}
