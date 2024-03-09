package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "ScannableItems")
public class ScannableItem {
    @Id
    @Column(name = "Item_ID")
    private String itemId;

    @ManyToOne
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @Column(name = "Item_Name", nullable = false)
    private String itemName;

    @Column(name = "Main_Barcode", nullable = false)
    private String mainBarcode;

    @Column(name = "Alt_Barcode")
    private String altBarcode;

    @Column(name = "Current_Count", nullable = false)
    private Integer currentCount;

    @Column(name = "Threshold", nullable = false)
    private Integer threshold;

    @Column(name = "Cost")
    private Float cost;

    public ScannableItem() {}

    public ScannableItem(Builder builder) {
        this.itemId = builder.itemId;
        this.user = builder.user;
        this.itemName = builder.itemName;
        this.mainBarcode = builder.mainBarcode;
        this.currentCount = builder.currentCount;
        builder.altBarcode.ifPresent(s -> this.altBarcode = s);
        builder.cost.ifPresent(cost -> this.cost = cost);
    }

    public static class Builder {
        private String itemId;
        private User user;
        private String itemName;
        private String mainBarcode;
        private Optional<String> altBarcode;
        private Integer currentCount;
        private Integer threshold;
        private Optional<Float> cost;

        public Builder(User user, String itemName, String mainBarcode,
                       Integer currentCount, Integer threshold) {
            this.itemId = UUID.randomUUID().toString();
            this.user = user;
            this.itemName = itemName;
            this.mainBarcode = mainBarcode;
            this.currentCount = currentCount;
            this.threshold = threshold;
            this.altBarcode = Optional.empty();
            this.cost = Optional.empty();
        }

        public Builder setAltBarcode(String altBarcode) {
            this.altBarcode = Optional.of(altBarcode);
            return this;
        }

        public Builder setCost(Float cost) {
            this.cost = Optional.of(cost);
            return this;
        }

        public ScannableItem build() {
            return new ScannableItem(this);
        }
    }
}
