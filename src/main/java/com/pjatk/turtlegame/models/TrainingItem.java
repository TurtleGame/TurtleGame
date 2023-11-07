package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "training_item")
@Setter
@Getter
public class TrainingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    private int howMany;

    @ManyToOne
    @JoinColumn(name = "trainItem_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;
}
