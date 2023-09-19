package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.models.UserItem;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private final UserRepository userRepository;

    public List<UserItem> getItems(int id) {
        User user = userRepository.findById(id);
        return user.getUserItemList();
    }

    public List<UserItem> getFood(int id) {
        User user = userRepository.findById(id);


        return user.getUserItemList().stream()
                .filter(item -> "Jedzenie".equals(item.getItem().getItemType().getName()))
                .collect(Collectors.toList());
    }

}
