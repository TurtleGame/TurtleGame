package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_statistic")
@Setter
@Getter
public class ItemStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "statistic_id")
    private Statistic statistic;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int value;
}
