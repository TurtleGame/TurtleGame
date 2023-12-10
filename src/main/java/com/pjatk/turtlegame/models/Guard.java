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

    @OneToMany(mappedBy = "winnerGuard")
    private List<TurtleBattleHistory> wonBattles;

    @OneToMany(mappedBy = "loserGuard")
    private List<TurtleBattleHistory> lostBattles;

    @OneToMany(mappedBy = "guard")
    private List<GuardItem> guardItemList;

    public int getHP() {
        return  5 * getGuardStatistics()
                .stream()
                .filter(guardStatistic -> guardStatistic.getStatistic().getId() == 1)
                .mapToInt(GuardStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getMP() {
        return getGuardStatistics()
                .stream()
                .filter(guardStatistic -> guardStatistic.getStatistic().getId() == 2)
                .mapToInt(GuardStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getAgility() {
        return getGuardStatistics()
                .stream()
                .filter(guardStatistic -> guardStatistic.getStatistic().getId() == 3)
                .mapToInt(GuardStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getStrength() {
        return getGuardStatistics()
                .stream()
                .filter(guardStatistic -> guardStatistic.getStatistic().getId() == 4)
                .mapToInt(GuardStatistic::getValue).findFirst()
                .orElse(0);
    }

    public int getStamina() {
        return getGuardStatistics()
                .stream()
                .filter(guardStatistic -> guardStatistic.getStatistic().getId() == 5)
                .mapToInt(GuardStatistic::getValue).findFirst()
                .orElse(0);
    }


}
