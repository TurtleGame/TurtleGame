package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "training")
@Setter
@Getter
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 40)
    private String name;

    @OneToMany(mappedBy = "training")
    private List<TrainingItem> trainingItemList;

    @OneToMany(mappedBy = "training")
    private List<TurtleTrainingHistory> turtleTrainingHistories;
}
