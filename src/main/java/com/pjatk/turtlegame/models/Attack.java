package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "attack")
@Setter
@Getter
public class Attack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    @Column(precision = 3, scale = 2)
    private double power;

    @ManyToOne
    @JoinColumn(name = "attack_type_id")
    private AttackType attackType;

    @OneToMany(mappedBy = "attack")
    private List<SpeciesAttack> speciesAttackList;

}
