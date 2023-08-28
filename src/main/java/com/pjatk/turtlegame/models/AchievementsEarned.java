package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievements_earned")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementsEarned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    private LocalDateTime wonAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private Achievement achievement;
}
