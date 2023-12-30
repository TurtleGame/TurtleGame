package com.pjatk.turtlegame.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "item")
@Setter
@Getter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @Size(min = 2, max = 100)
    private String description;

    private String slot;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private ItemType itemType;

    @ManyToOne
    @JoinColumn(name = "rarity_id")
    private Rarity rarity;

    @OneToMany(mappedBy = "item")
    private List<UserItem> userItemList;

    @OneToMany(mappedBy = "item")
    private List<ExpeditionItem> expeditionItemList;

    @OneToMany(mappedBy = "item")
    private List<GuardItem> guardItemList;

    @OneToMany(mappedBy = "item")
    private List<ItemStatistic> itemStatisticList;

    @OneToOne(mappedBy = "item")
    private TurtleType turtleType;

    @OneToMany(mappedBy = "item")
    @JsonIgnore
    private List<ItemOwnerMarket> itemOwnerMarketList;

    public ItemStatistic getItemStatistic(int id) {
        return itemStatisticList.stream()
                .filter(itemStatistic -> itemStatistic.getItem().getId() == id)
                .findFirst()
                .orElse(null);
    }
    public ItemStatistic getItemStatistic(){
        return itemStatisticList.stream()
                .filter(itemStatistic -> itemStatistic.getItem().getId() == (this).getId())
                .findFirst()
                .orElse(null);
    }

    public boolean isEquipment() {
        return this.getItemType().getName().equals("Zbroja");
    }

}
