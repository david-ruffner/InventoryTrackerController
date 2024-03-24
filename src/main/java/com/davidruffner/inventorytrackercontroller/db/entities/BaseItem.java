package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.Optional;
import java.util.UUID;

@MappedSuperclass
public class BaseItem {
    @Id
    @Column(name = "Item_ID")
    private String itemId;

    @Column(name = "Item_Name")
    private String itemName;

    @Column(name = "Cost")
    private Float cost;

    @ManyToOne()
    @JoinColumn(name = "User_ID")
    private User user;

    public BaseItem() {

    }

    public BaseItem(User user, String itemName, Float cost) {
        this.user = user;
        this.itemId = UUID.randomUUID().toString();
        this.itemName = itemName;
        this.cost = cost;
    }

    public BaseItem(User user, String itemName, Optional<Float> cost) {
        this.user = user;
        this.itemId = UUID.randomUUID().toString();
        this.itemName = itemName;
        cost.ifPresent(c -> this.cost = c);
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Optional<Float> getCost() {
        return Optional.ofNullable(this.cost);
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public User getUser() {
        return user;
    }

    public BaseItem setUser(User user) {
        this.user = user;
        return this;
    }
}
