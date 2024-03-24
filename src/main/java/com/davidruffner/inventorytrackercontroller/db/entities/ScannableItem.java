package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Optional;

@Entity
@Table(name = "ScannableItems")
public class ScannableItem extends BaseItem {
    @Column(name = "Main_Barcode")
    private String mainBarcode;

    @Column(name = "Alt_Barcode")
    private String altBarcode;

    @Column(name = "Current_Count")
    private Integer currentCount;

    @Column(name = "Threshold")
    private Integer threshold;

    public ScannableItem() {
        super();
    }

    public ScannableItem(User user, String itemName, Float cost, String mainBarcode,
                         String altBarcode, Integer currentCount, Integer threshold) {
        super(user, itemName, cost);
        this.mainBarcode = mainBarcode;
        this.altBarcode = altBarcode;
        this.currentCount = currentCount;
        this.threshold = threshold;
    }

    public String getMainBarcode() {
        return mainBarcode;
    }

    public ScannableItem setMainBarcode(String mainBarcode) {
        this.mainBarcode = mainBarcode;
        return this;
    }

    public String getAltBarcode() {
        return altBarcode;
    }

    public ScannableItem setAltBarcode(String altBarcode) {
        this.altBarcode = altBarcode;
        return this;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public ScannableItem setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
        return this;
    }

    public void incrementCurrentCount() {
        this.currentCount = this.currentCount + 1;
    }

    public void decrementCurrentCount() {
        this.currentCount = this.currentCount - 1;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public ScannableItem setThreshold(Integer threshold) {
        this.threshold = threshold;
        return this;
    }

    public ScannableItem(Builder builder) {
        super(builder.user, builder.itemName, builder.cost);
        this.mainBarcode = builder.mainBarcode;
        this.currentCount = builder.currentCount;
        this.threshold = builder.threshold;
        builder.altBarcode.ifPresent(s -> this.altBarcode = s);
    }

    public static class Builder {
        private User user;
        private String itemName;
        private Optional<Float> cost;
        private String mainBarcode;
        private Integer currentCount;
        private Integer threshold;
        private Optional<String> altBarcode;

        public Builder(User user, String itemName, String mainBarcode,
                       Integer currentCount, Integer threshold) {
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
