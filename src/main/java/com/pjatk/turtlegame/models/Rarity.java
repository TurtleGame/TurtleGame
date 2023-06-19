package com.pjatk.turtlegame.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Table(name = "rarity")
@Entity
@Setter
@Getter
public class Rarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 50)
    private String name;
@JsonIgnore
    @OneToMany(mappedBy = "rarity", fetch = FetchType.LAZY)
    private List<TurtleType> turtleTypeList;

}
