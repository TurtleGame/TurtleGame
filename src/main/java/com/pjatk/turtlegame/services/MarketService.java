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
            if (selling.isSelling() && selling.getEndAt() == null) {
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

    public List<ItemOwnerMarket> getAllItems (String sortField, Sort.Direction sortDir) {
        List<ItemOwnerMarket> history = itemOwnerMarketRepository.findAll();
        List<ItemOwnerMarket> items = new ArrayList<>();

        if (history.isEmpty()) return items;

        for(ItemOwnerMarket selling : history) {
            if (!selling.getItem().getItemType().getName().equals("Jajko")) {
                items.add(selling);
            }
        }

        return sortItems(sortField, sortDir, items, 0);
    }

    public List<ItemOwnerMarket> getAllEggs (String sortField, Sort.Direction sortDir) {
        List<ItemOwnerMarket> history = itemOwnerMarketRepository.findAll();
        List<ItemOwnerMarket> eggs = new ArrayList<>();

        if (history.isEmpty()) return eggs;

        for(ItemOwnerMarket selling : history) {
            if (selling.getItem().getItemType().getName().equals("Jajko")) {
                eggs.add(selling);
            }
        }

        return sortItems(sortField, sortDir, eggs, 1);
    }

    public List<ItemOwnerMarket> sortItems(String sortField, Sort.Direction sortDir, List<ItemOwnerMarket> itemsOwn, int egg) {
        switch (sortField) {
            case "turtle_id":
                if (sortDir.isDescending()) {
                    itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getId()).reversed());
                } else {
                    itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getId()));
                }
                break;
            case "name":
                if (sortDir.isDescending()) {
                    itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getName()).reversed());
                } else {
                    itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getName()));
                }
                break;
            case "type":
                if (egg == 1) {
                    if (sortDir.isDescending()) {
                        itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getTurtleType().getName()).reversed());
                    } else {
                        itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getTurtleType().getName()));
                    }
                }
                else {
                    if (sortDir.isDescending()) {
                        itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getItemType().getName()).reversed());
                    } else {
                        itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getItemType().getName()));
                    }
                }
                break;
            case "level":
            case "rarity":
                if (sortDir.isDescending()) {
                    itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getRarity().getName()).reversed());
                } else {
                    itemsOwn.sort(Comparator.comparing((ItemOwnerMarket item) -> item.getItem().getRarity().getName()));
                }
                break;
            case "price":
                if (sortDir.isDescending()) {
                    itemsOwn.sort(Comparator.comparing(ItemOwnerMarket::getHowMuch).reversed());
                } else {
                    itemsOwn.sort(Comparator.comparing(ItemOwnerMarket::getHowMuch));
                }
                break;
        }

        return itemsOwn;
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

    public int sellerIsBuyerItem (User user, ItemOwnerMarket item) {
        int userId = 0;

        for (ItemOwnerMarket selling : itemOwnerMarketRepository.findAll()) {
            if (selling.getId() == item.getId() && selling.getUser().getId() == user.getId()) {
                userId = selling.getUser().getId();
            }
        }

        if (user.getId() == userId)
            return 1;

        return 0;
    }

    public int getQuantity(ItemOwnerMarket item) {
        int quantity = 0;

        for (ItemOwnerMarket selling : itemOwnerMarketRepository.findAll()) {
            if (selling.getId() == item.getId()) {
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

    public int priceGold (ItemOwnerMarket item) {
        int gold = 0;

        for (ItemOwnerMarket selling : itemOwnerMarketRepository.findAll()) {
            if (selling.getId() == item.getId()) {
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
        privateMessage.setContent("Żółw " + turtle.getName() + " został sprzedany za " + priceShells(turtle) + " muszelek.");
        privateMessage.setRecipient(oldUser);
        privateMessage.setSentAt(LocalDateTime.now());
        privateMessage.setGold(0);
        privateMessageRepository.save(privateMessage);

        userRepository.save(oldUser);

        turtle.getTurtleOwnerHistoryList().stream()
                .filter(history -> history.getEndAt() == null)
                .forEach(history -> {
                    history.setEndAt(now);
                    turtleOwnerHistoryRepository.save(history);
                });



        turtle.setOwner(newUser);

        turtleOwnerHistory.setEndAt(null);
        turtleOwnerHistory.setStartAt(now);
        turtleOwnerHistory.setTurtle(turtle);
        turtleOwnerHistory.setUser(newUser);
        turtleOwnerHistory.setFirstOwner(0);
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
        User oldUser = itemOwnerMarketRepository.findById(itemId).get().getUser();
        ItemOwnerMarket transaction = itemOwnerMarketRepository.findById(itemId).get();
        Item item = transaction.getItem();
        PrivateMessage privateMessage = new PrivateMessage();

        if (newUser.getGold() < priceGold(transaction)) {

            throw new IllegalArgumentException("Brak wystarczającej ilości golda");

        }

        newUser.setGold(newUser.getGold() - priceGold(transaction));
        userRepository.save(newUser);

        oldUser.setGold(oldUser.getGold() + priceGold(transaction));
        privateMessage.setTitle("Przedmiot został sprzedany!");
        privateMessage.setContent("Przedmiot: " + item.getName() + " (" + getQuantity(transaction) + ") został sprzedany za " + priceGold(transaction) + " golda.");
        privateMessage.setRecipient(oldUser);
        privateMessage.setSentAt(LocalDateTime.now());
        privateMessage.setGold(0);
        privateMessageRepository.save(privateMessage);

        userRepository.save(oldUser);

        addItem(newUser, item, transaction);
    }

    @Transactional
    public void undoItem (int itemId, User user) {

        Item item = itemOwnerMarketRepository.findById(itemId).get().getItem();
        ItemOwnerMarket transaction = itemOwnerMarketRepository.findById(itemId).get();

        addItem(user, item, transaction);
    }

    private void addItem(User user, Item item, ItemOwnerMarket transaction) {

        for (ItemOwnerMarket selling : itemOwnerMarketRepository.findAll()) {
            if (selling.getId() == transaction.getId()) {
                itemOwnerMarketRepository.delete(selling);
            }
        }

        itemService.addItem(user, item, transaction.getQuantity());
    }
}
