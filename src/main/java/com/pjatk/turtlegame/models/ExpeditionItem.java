package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expedition_equipment")
@Setter
@Getter
public class ExpeditionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    private double chance;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;
}
