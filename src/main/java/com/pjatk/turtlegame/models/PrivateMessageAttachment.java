package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class PrivateMessageAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "private_message_id")
    private PrivateMessage privateMessage;

    @ManyToOne
    @JoinColumn(name = "turtle_expedition_history_id")
    private TurtleExpeditionHistory turtleExpeditionHistory;


}
