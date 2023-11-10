package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "turtle_owner_history")
@Setter
@Getter
public class TurtleOwnerHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @NotNull
    @Column(name = "isSelling", columnDefinition = "INT(1) DEFAULT 0")
    private boolean isSelling;

    private int howMuch;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

}
