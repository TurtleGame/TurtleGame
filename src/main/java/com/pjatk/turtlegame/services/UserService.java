package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.models.DTOs.UserDTO;
import com.pjatk.turtlegame.repositories.TurtleOwnerHistoryRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Turtle> getTurtles(User user) {
        List<TurtleOwnerHistory> turtleOwnerHistoryList = turtleOwnerHistoryRepository.findByUserAndEndAtIsNull(user);
        List<Turtle> turtles = new ArrayList<>();
        for (TurtleOwnerHistory item : turtleOwnerHistoryList) {
            turtles.add(item.getTurtle());
        }

        return turtles;
    }

    public void addNewUser(UserDTO userDTO) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail().trim());
        user.setUsername(userDTO.getUsername().trim());
        user.setGold(0);
        user.setRegistrationDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean isUsernameAlreadyTaken(String username) {
        User existingUser = userRepository.findUserByUsername(username);
        return existingUser != null;
    }

    public boolean isEmailAlreadyTaken(String email) {
        User existingUser = userRepository.findUserByEmail(email);
        return existingUser != null;
    }

    public List<String> searchUsers(String keyword, String username){
        return userRepository.searchUserByKeyword(keyword)
                .stream()
                .filter(username1 -> !username1.equals(username))
                .collect(Collectors.toList());

    }


}
