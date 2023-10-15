package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

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
    private String name;

    @NotNull
    private LocalDateTime hatchingAt;

    @Value("2")
    private int warming;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TurtleType turtleType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}