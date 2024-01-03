package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.exceptions.TurtleNotFoundException;
import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemStatisticRepository itemStatisticRepository;
    private final UserItemRepository userItemRepository;
    private final TurtleEggRepository turtleEggRepository;
    private final TurtleTypeRepository turtleTypeRepository;
    private final ItemOwnerMarketRepository itemOwnerMarketRepository;
    private final TurtleStaticsRepository turtleStaticsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Item> getFood(User user) {
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Jedzenie".equals(item.getItemType().getName()))
                .toList();
    }

    public boolean getItem(int itemId, int ownerId) {
        return userItemRepository.findByItemIdAndUserId(itemId, ownerId).isPresent();
    }

    public UserItem getItemDetails(int itemId, int ownerId) throws TurtleNotFoundException {

        return userItemRepository.findByItemIdAndUserId(itemId, ownerId)
                .orElseThrow(() -> new TurtleNotFoundException("Item not found"));

    }

    public void removeItem(User user, int itemId, int quantity) {
        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == itemId && entry.getTurtle() == null)
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
                .filter(entry -> entry.getItem().getId() == item.getId() && entry.getTurtle() == null)
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

    public void sellItem(int userId, int itemId, int gold, int quantity) {
        User user = userRepository.findById(userId);
        ItemOwnerMarket selling = new ItemOwnerMarket();

        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć przedmiotu o podanym ID"));


        selling(gold, quantity, user, selling, userItem);

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

    public void sellEgg(int userId, int eggId, int gold, int quantity) {
        User user = userRepository.findById(userId);
        ItemOwnerMarket selling = new ItemOwnerMarket();

        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == eggId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć jajka o podanym ID"));


        selling(gold, quantity, user, selling, userItem);
    }

    public void adoptEgg(User user, int eggId, String name) throws Exception {

        if (name.length() < 2 || name.length() > 50) {
            throw new Exception("Nieprawidłowe imię");
        }

        if(!user.canHaveMoreTurtles()){
            throw  new Exception("Nie możesz adoptować więcej żółwi!");
        }
        UserItem userItem = user.getUserItemList()
                .stream()
                .filter(entry -> entry.getItem().getId() == eggId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie można znaleźć jajka o podanym ID"));

        TurtleType turtleType = turtleTypeRepository.findById(userItem.getItem().getTurtleType().getId());


        TurtleEgg egg = new TurtleEgg();
        egg.setHatchingAt(LocalDateTime.now().plusHours(turtleType.getHatchingTime()));
        egg.setName(name);
        egg.setWarming(2);
        egg.setTurtleType(turtleType);
        egg.setUser(user);
        turtleEggRepository.save(egg);

        removeItem(user, userItem.getItem().getId(), 1);
    }

    private void selling(int gold, int quantity, User user, ItemOwnerMarket selling, UserItem userItem) {
        selling.setSelling(true);
        selling.setHowMuch(gold);
        selling.setQuantity(quantity);
        selling.setUser(user);
        selling.setItem(userItem.getItem());

        removeItem(user, userItem.getItem().getId(), quantity);

        itemOwnerMarketRepository.save(selling);
    }

    public List<Item> getAcademyItems(User user) {
        return user.getUserItemList().stream()
                .map(UserItem::getItem)
                .filter(item -> "Awansowanie".equals(item.getItemType().getName()))
                .toList();
    }

    public List<UserItem> getHelmets(User user, Turtle turtle) {
        List<UserItem> userItems = user.getUserItemList()
                .stream()
                .filter(item -> item.getItem().isEquipment() && item.getItem().getSlot().equals("Hełm") && item.getTurtle() == null)
                .toList();

        if (turtle.getHelmet() != null) {
            return userItems
                    .stream()
                    .filter(item -> item.getItem().getId() != turtle.getHelmet().getItem().getId())
                    .collect(Collectors.toList());
        }

        return userItems;

    }

    public List<UserItem> getBoots(User user, Turtle turtle) {
        List<UserItem> userItems = user.getUserItemList()
                .stream()
                .filter(item -> item.getItem().getItemType().getName().equals("Zbroja") && item.getItem().getSlot().equals("Buty"))
                .collect(Collectors.toList());

        if(turtle.getBoots() != null){
            return userItems
                    .stream()
                    .filter(item -> item.getItem().getId() != turtle.getBoots().getItem().getId())
                    .collect(Collectors.toList());
        }

        return userItems;
    }

    public List<UserItem> getSwords(User user, Turtle turtle) {
        List<UserItem> userItems = user.getUserItemList()
                .stream()
                .filter(item -> item.getItem().getItemType().getName().equals("Zbroja") && item.getItem().getSlot().equals("Miecz"))
                .collect(Collectors.toList());

        if(turtle.getSword() !=null){
            return userItems
                    .stream()
                    .filter(item -> item.getItem().getId() != turtle.getSword().getItem().getId())
                    .collect(Collectors.toList());
        }

        return userItems;
    }

    public List<UserItem> getWands(User user, Turtle turtle) {
        List<UserItem> userItems = user.getUserItemList()
                .stream()
                .filter(item -> item.getItem().getItemType().getName().equals("Zbroja") && item.getItem().getSlot().equals("Różdżka"))
                .collect(Collectors.toList());

        if(turtle.getWand() !=null){
            return userItems
                    .stream()
                    .filter(item -> item.getItem().getId() != turtle.getWand().getItem().getId())
                    .collect(Collectors.toList());
        }

        return userItems;
    }

    public void unequippedItem(User user, Turtle turtle) {
        List<UserItem> itemList = user.getUserItemList();

        for (UserItem userItem : itemList) {
            if (userItem.getTurtle() == null) {
                continue;
            }
            if (userItem.getTurtle().equals(turtle)) {
                userItemRepository.delete(userItem);
                addItem(user, userItem.getItem(), 1);
            }
        }
        entityManager.refresh(user);
    }

    @Transactional
    public void wearItems(User user, Integer helmetId, Integer weaponId, Integer wandId, Integer bootsId, Integer turtleId) {
        Turtle turtle = user.getTurtle(turtleId);
        unequippedItem(user, turtle);

        for (UserItem userItem : user.getUserItemList()) {
            if (userItem.getTurtle() != null) {
                continue;
            }
            Integer itemId = userItem.getItem().getId();

            if (itemId.equals(helmetId) || itemId.equals(wandId) || itemId.equals(weaponId) || itemId.equals(bootsId)) {
                removeItem(user, itemId, 1);

                UserItem itemOnTurtle = new UserItem();
                itemOnTurtle.setTurtle(turtle);
                itemOnTurtle.setQuantity(1);
                itemOnTurtle.setUser(user);
                itemOnTurtle.setItem(userItem.getItem());
                userItemRepository.save(itemOnTurtle);

                TurtleService.setStatistics(itemId, turtle, itemStatisticRepository, turtleStaticsRepository);
            }
        }
    }


}
