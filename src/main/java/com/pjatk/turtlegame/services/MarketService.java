package com.pjatk.turtlegame.services;

import com.pjatk.turtlegame.models.*;
import com.pjatk.turtlegame.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class MarketService {
    private TurtleOwnerHistoryRepository turtleOwnerHistoryRepository;
    private TurtleRepository turtleRepository;
    private UserRepository userRepository;
    private ItemService itemService;
    private ItemOwnerMarketRepository itemOwnerMarketRepository;
    private PrivateMessageRepository privateMessageRepository;

    public List<Turtle> getAllTurtles (String sortField, Sort.Direction sortDir){
        List<Turtle> turtles = new ArrayList<>();
        List<TurtleOwnerHistory> history = new ArrayList<>();
        Sort.Order order = new Sort.Order(sortDir, sortField);

        history = switch (sortField) {
            case "turtle_id" -> turtleOwnerHistoryRepository.findAll(Sort.by(order));
            case "price" -> turtleOwnerHistoryRepository.findAll(Sort.by(sortDir, "howMuch"));
            default -> history;
        };

        if (history.isEmpty()) {
            history = turtleOwnerHistoryRepository.findAll();
        }

        for (TurtleOwnerHistory selling : history) {
            if (selling.isSelling()) {
                turtles.add(selling.getTurtle());
            }
        }

        switch (sortField) {
            case "name":
                if (sortDir.isDescending()) {
                    turtles.sort(Comparator.comparing(Turtle::getName).reversed());
                } else {
                    turtles.sort(Comparator.comparing(Turtle::getName));
                }
               break;
            case "type":
                if (sortDir.isDescending()) {
                    turtles.sort(Comparator.comparing((Turtle turtle) -> turtle.getTurtleType().getName()).reversed());
                } else {
                    turtles.sort(Comparator.comparing((Turtle turtle) -> turtle.getTurtleType().getName()));
                }
               break;
            case "rarity":
            case "level":
                if (sortDir.isDescending()) {
                    turtles.sort(Comparator.comparing(Turtle::getLevel).reversed());
                } else {
                    turtles.sort(Comparator.comparing(Turtle::getLevel));
                }
               break;
        }

        return turtles;
    }

    public List<Item> getAllItems (String sortField, Sort.Direction sortDir) {
        List<ItemOwnerMarket> history = itemOwnerMarketRepository.findAll();
        List<ItemOwnerMarket> itemsOwn = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        if (history.isEmpty()) return items;

        for(ItemOwnerMarket selling : history) {
            if (!selling.getItem().getItemType().getName().equals("Jajko")) {
                items.add(selling.getItem());
                itemsOwn.add(selling);
            }
        }

        return sortItems(sortField, sortDir, items, itemsOwn);
    }

    public List<Item> getAllEggs (String sortField, Sort.Direction sortDir) {
        List<ItemOwnerMarket> history = itemOwnerMarketRepository.findAll();
        List<ItemOwnerMarket> eggOwn = new ArrayList<>();
        List<Item> eggs = new ArrayList<>();

        if (history.isEmpty()) return eggs;

        for(ItemOwnerMarket selling : history) {
            if (selling.getItem().getItemType().getName().equals("Jajko")) {
                eggs.add(selling.getItem());
                eggOwn.add(selling);
            }
        }

        return sortItems(sortField, sortDir, eggs, eggOwn);
    }

    public List<Item> sortItems(String sortField, Sort.Direction sortDir, List<Item> items, List<ItemOwnerMarket> itemsOwn) {
        switch (sortField) {
            case "turtle_id":
                if (sortDir.isDescending()) {
                    items.sort(Comparator.comparing(Item::getId).reversed());
                } else {
                    items.sort(Comparator.comparing(Item::getId));
                }
                break;
            case "name":
                if (sortDir.isDescending()) {
                    items.sort(Comparator.comparing(Item::getName).reversed());
                } else {
                    items.sort(Comparator.comparing(Item::getName));
                }
                break;
            case "type":
                if (sortDir.isDescending()) {
                    items.sort(Comparator.comparing((Item item) -> item.getItemType().getName()).reversed());
                } else {
                    items.sort(Comparator.comparing((Item item) -> item.getItemType().getName()));
                }
                break;
            case "level":
            case "rarity":
                if (sortDir.isDescending()) {
                    items.sort(Comparator.comparing((Item item) -> item.getRarity().getName()).reversed());
                } else {
                    items.sort(Comparator.comparing((Item item) -> item.getRarity().getName()));
                }
                break;
            case "price":
                if (sortDir.isDescending()) {
                    itemsOwn.sort(Comparator.comparing(ItemOwnerMarket::getHowMuch).reversed());
                } else {
                    itemsOwn.sort(Comparator.comparing(ItemOwnerMarket::getHowMuch));
                }
                items.clear();
                for (ItemOwnerMarket item : itemsOwn) {
                    items.add(item.getItem());
                }
                break;
        }

        return items;
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
        Optional<ItemOwnerMarket> selling = itemOwnerMarketRepository.findByItemIdAndUserId(item.getId(), user.getId());

        return selling.map(record -> {
            System.out.println(1);
            return 1;
        }).orElseGet(() -> {
            System.out.println(0);
            return 0;
        });
    }

    public int getQuantity(Item item, User user) {
        int quantity = 0;

        for (ItemOwnerMarket selling : item.getItemOwnerMarketList()) {
            if (selling.getEndAt() == null && selling.getUser().getId() == user.getId()) {
                quantity = selling.getQuantity();
            }
        }

        return quantity;
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

    public int priceGold (Item item, User user) {
        int gold = 0;

        for (ItemOwnerMarket selling : item.getItemOwnerMarketList()) {
            if (selling.getEndAt() == null && selling.getUser().getId() == user.getId()) {
                gold = selling.getHowMuch();
            }
        }

        return gold;
    }

    @Transactional
    public void buyTurtle (int turtleId, User newUser) {
        LocalDateTime now = LocalDateTime.now();
        User oldUser = turtleOwnerHistoryRepository.findByTurtleIdAndEndAtIsNull(turtleId).getUser();
        TurtleOwnerHistory transaction = turtleOwnerHistoryRepository.findByTurtleIdAndUserIdAndEndAtIsNull(turtleId, oldUser.getId());
        Turtle turtle = transaction.getTurtle();
        TurtleOwnerHistory turtleOwnerHistory = new TurtleOwnerHistory();
        PrivateMessage privateMessage = new PrivateMessage();

        if (newUser.getShells() < priceShells(turtle)) {

            throw new IllegalArgumentException("Brak wystarczającej ilości muszelek");

        }

        newUser.setShells(newUser.getShells() - priceShells(turtle));
        userRepository.save(newUser);

        oldUser.setShells(oldUser.getShells() + priceShells(turtle));
        privateMessage.setTitle("Żółw został sprzedany!");
        privateMessage.setContent("Żółw: " + turtle.getName() + " został sprzedany za " + priceShells(turtle) + " muszelek.");
        privateMessage.setRecipient(oldUser);
        privateMessage.setSentAt(LocalDateTime.now());
        privateMessage.setGold(0);
        privateMessageRepository.save(privateMessage);

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

    @Transactional
    public void buyItem (int itemId, User newUser) {
        User oldUser = itemOwnerMarketRepository.findByItemIdAndEndAtIsNull(itemId).getUser();
        ItemOwnerMarket transaction = itemOwnerMarketRepository.findByItemIdAndUserIdAndEndAtIsNull(itemId, oldUser.getId());
        Item item = transaction.getItem();
        PrivateMessage privateMessage = new PrivateMessage();

        if (newUser.getGold() < priceGold(item, oldUser)) {

            throw new IllegalArgumentException("Brak wystarczającej ilości golda");

        }

        newUser.setGold(newUser.getGold() - priceGold(item, oldUser));
        userRepository.save(newUser);

        oldUser.setGold(oldUser.getGold() + priceGold(item, oldUser));
        privateMessage.setTitle("Przedmiot został sprzedany!");
        privateMessage.setContent("Przedmiot: " + item.getName() + " (" + getQuantity(item, oldUser) + ") został sprzedany za " + priceGold(item, oldUser) + " golda.");
        privateMessage.setRecipient(oldUser);
        privateMessage.setSentAt(LocalDateTime.now());
        privateMessage.setGold(0);
        privateMessageRepository.save(privateMessage);

        userRepository.save(oldUser);

        addItem(newUser, item, transaction.getQuantity());
    }

    @Transactional
    public void undoItem (int itemId, User user) {

        Item item = itemOwnerMarketRepository.findByItemIdAndUserIdAndEndAtIsNull(itemId, user.getId()).getItem();
        ItemOwnerMarket transaction = itemOwnerMarketRepository.findByItemIdAndUserIdAndEndAtIsNull(itemId, user.getId());

        addItem(user, item, transaction.getQuantity());
    }

    private void addItem(User user, Item item, int quantity) {
        item.getItemOwnerMarketList().stream()
                .filter(history -> {
                    return history.getEndAt() == null && history.getUser().getId() == user.getId();
                })
                .forEach(history -> itemOwnerMarketRepository.delete(history));

        itemService.addItem(user, item, quantity);
    }
}
