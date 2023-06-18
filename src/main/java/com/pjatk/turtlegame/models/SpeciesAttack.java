package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "species_attack")
@Setter
@Getter
public class SpeciesAttack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "attack_id")
    private Attack attack;

    @ManyToOne
    @JoinColumn(name = "turtle_type_id")
    private TurtleType turtleType;

}
