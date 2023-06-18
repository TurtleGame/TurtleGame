package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "turtle_expedition_history")
@Setter
@Getter
public class TurtleExpeditionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

    @ManyToOne
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;
}
