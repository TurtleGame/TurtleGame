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
public class MarketService {
    private TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private TurtleRepository turtleRepository;
    private UserRepository userRepository;
    private ItemOwnerMarketRepository itemOwnerMarketRepository;
    private UserItemRepository userItemRepository;
    private PrivateMessageRepository privateMessageRepository;

    public List<Turtle> getAllTurtles (){
        List<TurtleOwnerHistory> history = turtleOwnerHistoryRepository.findAll();
        List<Turtle> turtles = new ArrayList<>();

        for (TurtleOwnerHistory selling : history) {
            if (selling.isSelling()) {
                turtles.add(selling.getTurtle());
            }
        }

        return turtles;
    }

    public List<Item> getAllItems () {
        List<ItemOwnerMarket> history = itemOwnerMarketRepository.findAll();
        List<Item> items = new ArrayList<>();

        if (history.isEmpty()) return items;

        for(ItemOwnerMarket selling : history) {
            if (!selling.getItem().getItemType().getName().equals("Jajko")) {
                items.add(selling.getItem());
            }
        }

        return items;
    }

    public List<Item> getAllEggs () {
        List<ItemOwnerMarket> history = itemOwnerMarketRepository.findAll();
        List<Item> eggs = new ArrayList<>();

        if (history.isEmpty()) return eggs;

        for(ItemOwnerMarket selling : history) {
            if (selling.getItem().getItemType().getName().equals("Jajko")) {
                eggs.add(selling.getItem());
            }
        }

        return eggs;
    }

    public int sellerIsBuyer (User user, Turtle turtle){
        List<TurtleOwnerHistory> history = turtleOwnerHistoryRepository.findAll();
        int userId = 0;

        for (TurtleOwnerHistory hist : history) {
            if (turtle.getId() == hist.getTurtle().getId()) {
                if (hist.getEndAt() == null)
                    userId = hist.getUser().getId();
            }
        }

        if (user.getId() == userId)
            return 1;

        return 0;
    }

    public int sellerIsBuyerItem (User user, Item item) {
        List<ItemOwnerMarket> market = itemOwnerMarketRepository.findAll();
        int userId = 0;

        for (ItemOwnerMarket selling : market) {
            if(item.getId() == selling.getItem().getId()) {
                if (selling.getEndAt() == null)
                    userId = selling.getUser().getId();
            }
        }

        if (user.getId() == userId) {
            return 1;
        }

        return 0;
    }

    public int priceShells (Turtle turtle) {
        int shells = 0;

        for (TurtleOwnerHistory selling : turtle.getTurtleOwnerHistoryList()) {
            if (selling.getEndAt() == null) {
                shells = selling.getHowMuch();
            }
        }

        return shells;
    }

    public int priceGold (Item item) {
        int gold = 100;



        for (ItemOwnerMarket selling : item.getItemOwnerMarketList()) {
            if (selling.getEndAt() == null) {
                gold = selling.getHowMuch();
            }
        }

        return gold;
    }

    public void buyTurtle (int turtleId, User newUser) {
        LocalDateTime now = LocalDateTime.now();
        User oldUser = turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtleId).getUser();
        TurtleOwnerHistory transaction = turtleOwnerHistoryRepository.findByTurtleIdAndUserIdAndEndAtIsNull(turtleId, oldUser.getId());
        Turtle turtle = transaction.getTurtle();
        TurtleOwnerHistory turtleOwnerHistory = new TurtleOwnerHistory();

        if (newUser.getShells() > priceShells(turtle)) {

            newUser.setShells(newUser.getShells() - priceShells(turtle));
            userRepository.save(newUser);

        } else if (newUser.getShells() == priceShells(turtle)) {

            newUser.setShells(0);
            userRepository.save(newUser);

        } else {

            throw new IllegalArgumentException("Brak wystarczającej ilości muszelek");

        }

        oldUser.setShells(oldUser.getShells() + priceShells(turtle));

        userRepository.save(oldUser);

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setEndAt(now);
                    history.setSelling(false);
                    turtleOwnerHistoryRepository.save(history);
                });



        turtle.setOwner(newUser);

        turtleOwnerHistory.setEndAt(null);
        turtleOwnerHistory.setStartAt(now);
        turtleOwnerHistory.setTurtle(turtle);
        turtleOwnerHistory.setUser(newUser);
        turtleOwnerHistory.setHowMuch(priceShells(turtle));

        turtleOwnerHistoryRepository.save(turtleOwnerHistory);
        turtleRepository.save(turtle);
    }

    public void undoTurtle (int turtleId, User user) {
        Turtle turtle = turtleOwnerHistoryRepository.findByTurtleIdAndUserIdAndEndAtIsNull(turtleId, user.getId()).getTurtle();

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setSelling(false);
                    turtleOwnerHistoryRepository.save(history);
                });

        turtle.setOwner(user);
        turtleRepository.save(turtle);
    }

    public void buyItem (int itemId, User newUser) {
        User oldUser = itemOwnerMarketRepository.findByItemIdAndEndAtIsNull(itemId).getUser();
        ItemOwnerMarket transaction = itemOwnerMarketRepository.findByItemIdAndUserIdAndEndAtIsNull(itemId, oldUser.getId());
        Item item = transaction.getItem();
        PrivateMessage privateMessage = new PrivateMessage();

        if (newUser.getGold() > priceGold(item)) {

            newUser.setGold(newUser.getGold() - priceGold(item));
            userRepository.save(newUser);

        } else if (newUser.getGold() == priceGold(item)) {

            newUser.setGold(0);
            userRepository.save(newUser);

        } else {

            throw new IllegalArgumentException("Brak wystarczającej ilości golda");

        }

        oldUser.setGold(oldUser.getGold() + priceGold(item));
        privateMessage.setTitle("Przedmiot został sprzedany!");
        privateMessage.setContent("Przedmiot: " + item.getName() + " został sprzedany za " + priceGold(item) + " golda.");
        privateMessage.setRecipient(oldUser);
        privateMessage.setSentAt(LocalDateTime.now());
        privateMessageRepository.save(privateMessage);

        userRepository.save(oldUser);

        item.getItemOwnerMarketList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    itemOwnerMarketRepository.delete(history);
                });

        List<UserItem> userItemList = newUser.getUserItemList();

        UserItem userItem = userItemList
                .stream()
                .filter(entry -> entry.getItem().getId() == (item.getId()))
                .findFirst().orElse(null);


        if (userItem == null) {
            userItem = new UserItem();
            userItem.setItem(item);
            userItem.setUser(newUser);
            userItem.setQuantity(1);
        } else {
            userItem.setQuantity(userItem.getQuantity() + 1);
        }

        userItemRepository.save(userItem);
    }

    public void undoItem (int itemId, User user) {

        Item item = itemOwnerMarketRepository.findByItemIdAndUserIdAndEndAtIsNull(itemId, user.getId()).getItem();

        item.getItemOwnerMarketList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    itemOwnerMarketRepository.delete(history);
                });

        List<UserItem> userItemList = user.getUserItemList();

        UserItem userItem = userItemList
                .stream()
                .filter(entry -> entry.getItem().getId() == (item.getId()))
                .findFirst().orElse(null);

        if (userItem == null) {
            userItem = new UserItem();
            userItem.setItem(item);
            userItem.setUser(user);
            userItem.setQuantity(1);
        } else {
            userItem.setQuantity(userItem.getQuantity() + 1);
        }

        userItemRepository.save(userItem);
    }
}
