package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "turtle_expedition_history")
@Setter
@Getter
public class TurtleExpeditionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;

    private int goldGained;

    @OneToMany(mappedBy = "turtleExpeditionHistory")
    private List<PrivateMessageAttachment> privateMessageAttachments;

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

    @ManyToOne
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;

    @NotNull
    @Column(name = "wasRewarded", columnDefinition = "INT(1) DEFAULT 0")
    private boolean wasRewarded;

}
