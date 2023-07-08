package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;


    @Size(max = 300)
    private String activationToken;


    private LocalDateTime activationTokenExpireAt;

    @NotNull
    @Size(min = 2, max = 15)
    private String username;

    @NotNull
    @Size(min = 2, max = 64)
    private String password;

    @NotNull
    @Size(min = 2, max = 50)
    private String email;

    @NotNull
    private int isEmailConfirmed;

    private LocalDateTime birthdate;

    @Size(max = 300)
    private String avatarPath;

    @NotNull
    private Integer gold;

    private String about;

    @Size(max = 50)
    private String city;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<TurtleOwnerHistory> turtleOwnerHistoryList;

    @OneToMany(mappedBy = "giver")
    private List<UserStatus> giverStatusList;

    @OneToMany(mappedBy = "user")
    private List<UserStatus> userStatusList;

    @OneToMany(mappedBy = "user")
    private List<ChatEntry> chatEntryList;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Report> reportList;

    @OneToMany(mappedBy = "sender")
    private List<PrivateMessage> sendPrivateMessageList;

    @OneToMany(mappedBy = "recipient")
    private List<PrivateMessage> recipientPrivateMessageList;

    @OneToMany(mappedBy = "user")
    private List<UserEquipmentHistory> userEquipmentHistoryList;

    @OneToMany(mappedBy = "pastOwner")
    private List<TurtleTransationHistory> pastOwnerHistoryList;

    @OneToMany(mappedBy = "newOwner")
    private List<TurtleTransationHistory> newOwnerHistoryList;

    @OneToMany(mappedBy = "user")
    private List<TurtleOwnerHistory> ownerHistoryList;
}
