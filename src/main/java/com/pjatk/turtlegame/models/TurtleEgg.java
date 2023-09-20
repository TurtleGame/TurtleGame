package com.pjatk.turtlegame.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "turtle_egg")
@Setter
@Getter
public class TurtleEgg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    private LocalDateTime hatchingAt;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TurtleType turtleType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}