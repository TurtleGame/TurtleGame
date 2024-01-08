package com.pjatk.turtlegame.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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


    @Column(unique = true)
    private String email;

    @NotNull
    private boolean isEmailConfirmed;

    @NotNull
    private Integer gold;

    @NotNull
    private Integer shells;

    @Column(columnDefinition = "TEXT")
    private String about;

    private LocalDateTime registrationDate;

    private LocalDateTime lastActivity;

    private int turtleLimit;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<TurtleOwnerHistory> turtleOwnerHistoryList;

    @OneToMany(mappedBy = "giver")
    private List<UserStatus> giverStatusList;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserStatus> userStatusList;

    @OneToMany(mappedBy = "sender")
    private List<PrivateMessage> sendPrivateMessageList;

    @OneToMany(mappedBy = "recipient")
    private List<PrivateMessage> recipientPrivateMessageList;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @OrderBy("item.id ASC")
    private List<UserItem> userItemList;

    @OneToMany(mappedBy = "user")
    private List<TurtleOwnerHistory> ownerHistoryList;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Turtle> turtles;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<TurtleEgg> eggs;

    @OneToMany(mappedBy = "user")
    private List<AchievementsEarned> achievementsEarnedList;

    @OneToMany(mappedBy = "receiver")
    private List<FriendRequest> receivedFriendRequests;

    @OneToMany(mappedBy = "sender")
    private List<FriendRequest> sentFriendRequests;

    @OneToMany(mappedBy = "author")
    private List<News> newsList;


    public List<PrivateMessage> getRecipientPrivateMessageList() {
        return recipientPrivateMessageList.stream()
                .sorted(Comparator.comparing(PrivateMessage::getSentAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PrivateMessage> getSendPrivateMessageList() {
        return sendPrivateMessageList.stream()
                .sorted(Comparator.comparing(PrivateMessage::getSentAt).reversed())
                .collect(Collectors.toList());
    }

    public AchievementsEarned getAchievementEarned(int id) {
        return achievementsEarnedList.stream()
                .filter(achievement -> achievement.getAchievement().getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Turtle getTurtle(Integer turtleId) {
        return getTurtles()
                .stream()
                .filter(turtle1 -> turtle1.getId() == turtleId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć żółwia o podanym ID"));
    }

    public boolean canHaveMoreTurtles() {
        return getTurtles().size() + getEggs().size() < turtleLimit;
    }

    public boolean isUserHaveUnreadMessage() {
        return recipientPrivateMessageList.stream().anyMatch(message -> !message.isRead());
    }

    public boolean isUserHaveTurtleOnExpedition() {
        for (Turtle turtle : turtles) {
            if (turtle.getCurrentExpedition() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserHaveTurtleOnTraining() {
        for (Turtle turtle : turtles) {
            if (turtle.getCurrentTraining() != null) {
                return true;
            }
        }
        return false;
    }

    public List<FriendRequest> getReceivedFriendRequests() {
        return receivedFriendRequests.stream()
                .filter(friendRequest -> !friendRequest.isStatus())
                .toList();
    }

    public List<FriendRequest> getAllFriendRequests() {
        return receivedFriendRequests.stream().toList();
    }

    public boolean isAccountBanned() {
        List<UserStatus> isAccountBanned = userStatusList
                .stream()
                .filter(status -> status.getEndAt().isAfter(LocalDateTime.now()))
                .toList();

        return !isAccountBanned.isEmpty();
    }

    public boolean hasAvatar() {
        String avatarPath = "/avatars/" + id + ".png";
        File avatarFile = new File(avatarPath);
        return avatarFile.exists();
    }
}
