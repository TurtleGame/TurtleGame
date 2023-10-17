package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.repositories.ItemRepository;
import com.pjatk.turtlegame.repositories.RoleRepository;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    ItemService itemService;

    @Transactional
    public void addNewUser(UserDTO userDTO) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail().trim());
        user.setUsername(userDTO.getUsername().trim());
        user.setGold(0);
        user.setRegistrationDate(LocalDateTime.now());
        user.setUserItemList(null);
        user.setRole(roleRepository.findById(2).orElseThrow(null));
        userRepository.save(user);
        Item item = itemRepository.findById(9).orElseThrow(null);
        Item egg = itemRepository.findById(21).orElseThrow(null);
        itemService.addItem(user, item, 5);
        itemService.addItem(user, egg, 1);
    }

    public boolean isUsernameAlreadyTaken(String username) {
        User existingUser = userRepository.findUserByUsername(username);
        return existingUser != null;
    }

    public boolean isEmailAlreadyTaken(String email) {
        User existingUser = userRepository.findUserByEmail(email);
        return existingUser != null;
    }

    public List<String> searchUsers(String keyword, String username) {
        return userRepository.searchUserByKeyword(keyword)
                .stream()
                .filter(username1 -> !username1.equals(username))
                .collect(Collectors.toList());

    }

    @Transactional
    public void editAbout(String about, User user) {
        user.setAbout(about);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserActivity(User user) {
        user.setLastActivity(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean isUserOnline(User user) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(user.getLastActivity(), currentTime);
        long timeSinceLastActivity = duration.toMillis();
        int onlineThresholdInMilliseconds = 5 * 6 * 1000;

        return timeSinceLastActivity <= onlineThresholdInMilliseconds;

    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        final BCryptPasswordEncoder passwordEncoder1 = new BCryptPasswordEncoder();

        if (!passwordEncoder1.matches(oldPassword, user.getPassword())) {
            return false;
        }
        if (newPassword.length() < 6) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;

    }


}

