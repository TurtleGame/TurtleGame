package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.exceptions.UnauthorizedAccessException;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemStatisticRepository itemStatisticRepository;
    private final UserItemRepository userItemRepository;
    private final TurtleEggRepository turtleEggRepository;
    private final TurtleTypeRepository turtleTypeRepository;
    private final ItemOwnerMarketRepository itemOwnerMarketRepository;

    public List<Item> getFood (User user) {
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Jedzenie".equals(item.getItemType().getName()))
                .toList();
    }

    public boolean getItem (int itemId, int ownerId) {
        if (userItemRepository.findByItemIdAndUserId(itemId, ownerId).isPresent())
            return true;
        return false;
    }

    public UserItem getItemDetails (int itemId, int ownerId) throws TurtleNotFoundException, UnauthorizedAccessException {

        return userItemRepository.findByItemIdAndUserId(itemId, ownerId)
                .orElseThrow(() -> new TurtleNotFoundException("Item not found"));

    }

    public void removeItem (User user, int itemId, int quantity) {
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

    public void addItem(User user, Item item, int quantity) {

        List<UserItem> userItemList = user.getUserItemList();

        UserItem userItem = userItemList
                .stream()
                .filter(entry -> entry.getItem().getId() == (item.getId()))
                .findFirst().orElse(null);

        if (userItem == null) {
            userItem = new UserItem();
            userItem.setItem(item);
            userItem.setUser(user);
            userItem.setQuantity(quantity);
        } else {
            userItem.setQuantity(userItem.getQuantity() + quantity);
        }

        userItemRepository.save(userItem);
    }

    public void sellItem (int userId, int itemId, int gold) {
        User user = userRepository.findById(userId);
        ItemOwnerMarket selling = new ItemOwnerMarket();

        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć przedmiotu o podanym ID"));


        selling.setSelling(true);
        selling.setHowMuch(gold);
        selling.setUser(user);
        selling.setItem(userItem.getItem());
        itemOwnerMarketRepository.save(selling);

        if (userItem.getQuantity() > 1) {
            userItem.setQuantity(userItem.getQuantity() - 1);
            userItemRepository.save(userItem);
        } else if (userItem.getQuantity() == 1) {
            userItemRepository.delete(userItem);
        } else {
            throw new IllegalArgumentException("Brak przedmiotu");
        }

    }

    public List<ItemStatistic> getItemsStatistics() {
        return itemStatisticRepository.findAll();
    }

    public List<Item> getEggs(int userId) {
        User user = userRepository.findById(userId);
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Jajko".equals(item.getItemType().getName()))
                .toList();
    }

    public void sellEgg(int userId, int eggId, int gold) {
        User user = userRepository.findById(userId);
        ItemOwnerMarket selling = new ItemOwnerMarket();

        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == eggId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć jajka o podanym ID"));


        selling.setSelling(true);
        selling.setHowMuch(gold);
        selling.setUser(user);
        selling.setItem(userItem.getItem());
        itemOwnerMarketRepository.save(selling);

        if (userItem.getQuantity() > 1) {
            userItem.setQuantity(userItem.getQuantity() - 1);
            userItemRepository.save(userItem);
        } else if (userItem.getQuantity() == 1) {
            userItemRepository.delete(userItem);
        } else {
            throw new IllegalArgumentException("Brak jajka");
        }

    }

    public void adoptEgg(User user, int eggId, String name) {
        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == eggId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć jajka o podanym ID"));

        TurtleType turtleType = turtleTypeRepository.findById(userItem.getItem().getItemType().getId());

        TurtleEgg egg = new TurtleEgg();
        egg.setHatchingAt(LocalDateTime.of(2024, 12, 12, 12, 12));
        egg.setName(name);
        egg.setWarming(2);
        egg.setTurtleType(turtleType);
        egg.setUser(user);
        turtleEggRepository.save(egg);

        removeItem(user, userItem.getItem().getId(), 1);
    }

    public List<Item> getAcademyItems(User user) {
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Awansowanie".equals(item.getItemType().getName()))
                .toList();
    }
}
