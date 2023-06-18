package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "expedition")
@Setter
@Getter
public class Expedition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 40)
    private String name;

    @NotNull
    @Size(min = 2, max = 40)
    private String description;

    @NotNull
    @Column(precision = 10, scale = 2)
    private double gold;

    @NotNull
    private int minLevel;

    @OneToMany(mappedBy = "expedition")
    private List<ExpeditionEquipment> expeditionEquipmentList;

    @OneToMany(mappedBy = "expedition")
    private List<TurtleExpeditionHistory> turtleExpeditionHistoryList;
}
