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
    ItemRepository itemRepository;
    RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    ItemService itemService;

    public List<Turtle> getTurtles(User user) {
        List<TurtleOwnerHistory> turtleOwnerHistoryList = turtleOwnerHistoryRepository.findByUserAndEndAtIsNull(user);
        List<Turtle> turtles = new ArrayList<>();
        for (TurtleOwnerHistory item : turtleOwnerHistoryList) {
            turtles.add(item.getTurtle());
        }

        return turtles;
    }

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


}

