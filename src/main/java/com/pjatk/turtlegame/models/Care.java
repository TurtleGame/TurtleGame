package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "care")
@Setter
@Getter
public class Care {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    private LocalDateTime lastCareDateAt;

    @NotNull
    @Size(min = 2, max = 50)
    private String turtleStatus;

    @OneToMany(mappedBy = "care")
    private List<TurtleCare> turtleCareList;
}
