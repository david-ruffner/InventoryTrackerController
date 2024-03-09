package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "NonScannableItems")
public class NonScannableItem {
    @Id
    @Column(name = "Item_ID")
    private String itemId;

    @ManyToOne
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @Column(name = "Item_Name", nullable = false)
    private String itemName;

    @Column(name = "Current_Count", nullable = false)
    private Integer currentCount;

    @Column(name = "Threshold", nullable = false)
    private Integer threshold;

    @Column(name = "Cost")
    private Float cost;

    public NonScannableItem() {}

    public NonScannableItem(Builder builder) {
        this.itemId = builder.itemId;
        this.user = builder.user;
        this.itemName = builder.itemName;
        this.currentCount = builder.currentCount;
        builder.cost.ifPresent(cost -> this.cost = cost);
    }

    public static class Builder {
        private String itemId;
        private User user;
        private String itemName;
        private Integer currentCount;
        private Integer threshold;
        private Optional<Float> cost;

        public Builder(User user, String itemName, Integer currentCount, Integer threshold) {
            this.itemId = UUID.randomUUID().toString();
            this.user = user;
            this.itemName = itemName;
            this.currentCount = currentCount;
            this.threshold = threshold;
            this.cost = Optional.empty();
        }

        public Builder setCost(Float cost) {
            this.cost = Optional.of(cost);
            return this;
        }

        public NonScannableItem build() {
            return new NonScannableItem(this);
        }
    }
}