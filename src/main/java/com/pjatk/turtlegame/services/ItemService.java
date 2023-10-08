package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemStatisticRepository itemStatisticRepository;
    private final UserItemRepository userItemRepository;
    private final TurtleEggRepository turtleEggRepository;
    private final TurtleTypeRepository turtleTypeRepository;


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

    public void addItem(User user, Item item, int quantity){

        List<UserItem> userItemList = user.getUserItemList();

        if (userItemList == null) {
            userItemList = new ArrayList<>();
            user.setUserItemList(userItemList);
        }

        UserItem userItem = userItemList
                .stream()
                .filter(entry -> entry.getItem().equals(item))
                .findFirst().orElse(null);

        if(userItem == null){
            userItem = new UserItem();
            userItem.setItem(item);
            userItem.setUser(user);
            userItem.setQuantity(quantity);
        } else {
            userItem.setQuantity(userItem.getQuantity()+quantity);
        }

        userItemRepository.save(userItem);
    }

    public List<ItemStatistic> getItemsStatistics(){
        return itemStatisticRepository.findAll();
    }

    public List<Item> getEggs(int id) {
        User user = userRepository.findById(id);
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Jajko".equals(item.getItemType().getName()))
                .toList();
    }

    public void abandonEgg(int userId, int eggId) {
        User user = userRepository.findById(userId);

        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == eggId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć jajka o podanym ID"));

        if (userItem.getQuantity() > 1) {
            userItem.setQuantity(userItem.getQuantity() - 1);
            userItemRepository.save(userItem);
        } else if (userItem.getQuantity() == 1) {
            userItemRepository.delete(userItem);
        } else {
            throw new IllegalArgumentException("Brak jajka");
        }
    }

    public void adoptEgg(int userId, int eggId, String name) {
        User user = userRepository.findById(userId);

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

        if (userItem.getQuantity() > 1) {
            userItem.setQuantity(userItem.getQuantity() - 1);
            userItemRepository.save(userItem);
        } else if (userItem.getQuantity() == 1) {
            userItemRepository.delete(userItem);
            // skoro to item to jak dla mnie można zostawić w bazie, bo imię ustawiamy dopiero przy przejściu na bycie jajkiem
            //itemRepository.deleteById(eggId);
        } else {
            throw new IllegalArgumentException("Brak jajka");
        }
    }


}
