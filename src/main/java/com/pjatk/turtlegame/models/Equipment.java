package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "equipment")
@Setter
@Getter
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @Size(min = 2, max = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    private EquipmentSlot slot;

    private Integer attack;

    private Integer defence;

    @OneToMany(mappedBy = "equipment")
    private List<UserEquipmentHistory> userEquipmentHistoryList;

    @OneToMany(mappedBy = "equipment")
    private List<ExpeditionEquipment> expeditionEquipmentList;
}
