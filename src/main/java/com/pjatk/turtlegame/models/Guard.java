package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "guard")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Guard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @NotNull
    @Size(min = 2)
    private String description;

    @OneToMany(mappedBy = "guard", fetch = FetchType.EAGER)
    private List<GuardStatistic> guardStatistics;


}
