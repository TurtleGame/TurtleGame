package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.ItemStatistic;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.models.UserItem;
import com.pjatk.turtlegame.repositories.ItemRepository;
import com.pjatk.turtlegame.repositories.ItemStatisticRepository;
import com.pjatk.turtlegame.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemStatisticRepository itemStatisticRepository;


    public List<UserItem> getItems(int id) {
        User user = userRepository.findById(id);
        return user.getUserItemList();
    }

    public List<Item> getFood(int id) {
        User user = userRepository.findById(id);
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Jedzenie".equals(item.getItemType().getName()))
                .toList();


    }

    public List<ItemStatistic> getItemsStatistics(){
        return itemStatisticRepository.findAll();
    }
}
