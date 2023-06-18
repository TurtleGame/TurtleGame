package com.pjatk.turtlegame.models;

import jakarta.persistence.*;

@Entity
@Table(name = "turtle_care")
public class TurtleCare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "care_id")
    private Care care;

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

}
