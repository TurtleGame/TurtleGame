package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "turtle_statistic")
@Setter
@Getter
public class TurtleStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    private int value;

    @ManyToOne
    @JoinColumn(name = "statistic_id")
    private Statistic statistic;

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;
}
