package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "private_message")
@Setter
@Getter
public class PrivateMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotNull
    @Size(min = 2, max = 250)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    private LocalDateTime sentAt;

    private Integer gold;

    private Integer shells;

    @Column(name = "is_read", columnDefinition = "INT(0)")
    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "turtle_id")
    private Turtle turtle;

    @OneToMany(mappedBy = "privateMessage", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<PrivateMessageAttachment> attachment;

}
