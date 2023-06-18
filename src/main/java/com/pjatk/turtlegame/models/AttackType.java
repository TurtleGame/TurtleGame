package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "attack_type")
@Setter
@Getter
public class AttackType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Size(min = 2, max = 50)
    @NotNull
    private String name;

    @OneToMany(mappedBy = "attackType")
    private List<Attack> attackList;

}
