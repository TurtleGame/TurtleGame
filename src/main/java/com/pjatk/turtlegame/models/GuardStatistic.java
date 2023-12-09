package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "guard_statistic")
@Setter
@Getter
public class GuardStatistic {
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
    @JoinColumn(name = "guard_id")
    private Guard guard;

}
