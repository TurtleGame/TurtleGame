package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "statistic")
@Setter
@Getter
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @OneToMany(mappedBy = "statistic")
    private List<TurtleStatistic> turtleStatisticList;

    @OneToMany(mappedBy = "statistic")
    private List<GuardStatistic> guardsStatistics;

    @OneToMany(mappedBy = "statistic")
    private List<Training> trainingList;

}
