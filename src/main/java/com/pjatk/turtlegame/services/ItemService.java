package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.Item;
import com.pjatk.turtlegame.models.ItemStatistic;
import com.pjatk.turtlegame.models.User;
import com.pjatk.turtlegame.models.UserItem;
import com.pjatk.turtlegame.repositories.ItemRepository;
import com.pjatk.turtlegame.repositories.ItemStatisticRepository;
import com.pjatk.turtlegame.repositories.UserItemRepository;
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
    private final UserItemRepository userItemRepository;


    public List<UserItem> getItems(int userId) {
        User user = userRepository.findById(userId);
        return user.getUserItemList();
    }

    public List<Item> getFood(int userId) {
        User user = userRepository.findById(userId);
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Jedzenie".equals(item.getItemType().getName()))
                .toList();


    }

    public void removeItem(int userId, int itemId, int quantity) {
        User user = userRepository.findById(userId);

        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć przedmiotu użytkownika o podanym ID"));

        if (userItem.getQuantity() > quantity) {

            userItem.setQuantity(userItem.getQuantity() - quantity);
            userItemRepository.save(userItem);

        } else if (userItem.getQuantity() == quantity) {
            userItemRepository.delete(userItem);
        } else {
            throw new IllegalArgumentException("Brak wystarczającej ilości");
        }


    }

    public List<ItemStatistic> getItemsStatistics() {
        return itemStatisticRepository.findAll();
    }
}
