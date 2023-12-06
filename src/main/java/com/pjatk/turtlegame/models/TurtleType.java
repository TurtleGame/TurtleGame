package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "turtle_type")
@Setter
@Getter
public class TurtleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    @Size(min = 2, max = 250)
    private String description;

    @NotNull
    private int hatchingTime;

    @OneToMany(mappedBy = "turtleType")
    private List<SpeciesAttack> speciesAttackList;

    @ManyToOne
    @JoinColumn(name = "rarity_id")
    private Rarity rarity;

    /*@ManyToOne
    @JoinColumn(name = "egg_item_id")
    private Item egg;*/

    @OneToMany(mappedBy = "turtleType")
    private List<Turtle> turtleList;

}
