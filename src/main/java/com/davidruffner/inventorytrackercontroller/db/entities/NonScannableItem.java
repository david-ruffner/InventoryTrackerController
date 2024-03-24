package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Table(name = "NonScannableItems")
public class NonScannableItem extends BaseItem {
    @ManyToOne()
    @JoinColumn(name = "Manufacturer_ID")
    private Manufacturer manufacturer;

    @Column(name = "Model_Name")
    private String modelName;

    @Column(name = "Color_Hex")
    private String colorHex;

    @Column(name = "Serial_Number")
    private String serialNumber;

    @OneToMany(cascade = ALL, orphanRemoval = true)
    private List<ItemPicture> itemPictures = new ArrayList<>();

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public String getModelName() {
        return modelName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public List<ItemPicture> getItemPictures() {
        return itemPictures;
    }

    public NonScannableItem setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public NonScannableItem setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    public NonScannableItem setColorHex(String colorHex) {
        this.colorHex = colorHex;
        return this;
    }

    public NonScannableItem setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public void addItemPicture(ItemPicture itemPicture) {
        this.itemPictures.add(itemPicture);
    }

    public void removeItemPicture(ItemPicture itemPicture) {
        this.itemPictures.remove(itemPicture);
    }

    public NonScannableItem setItemPictures(List<ItemPicture> itemPictures) {
        this.itemPictures = itemPictures;
        return this;
    }

    public NonScannableItem() {}

    public NonScannableItem(Builder builder) {
        super(builder.user, builder.itemName, builder.cost);
        builder.manufacturer.ifPresent(m -> this.manufacturer = m);
        builder.modelName.ifPresent(s -> this.modelName = s);
        builder.colorHex.ifPresent(s -> this.colorHex = s);
        builder.serialNumber.ifPresent(s -> this.serialNumber = s);
        builder.itemPictures.ifPresent(s -> this.itemPictures = s);
    }

    public static class Builder {
        private User user;
        private String itemName;
        private Optional<Float> cost = Optional.empty();
        private Optional<Manufacturer> manufacturer = Optional.empty();
        private Optional<String> modelName = Optional.empty();
        private Optional<String> colorHex = Optional.empty();
        private Optional<String> serialNumber = Optional.empty();
        private Optional<List<ItemPicture>> itemPictures = Optional.empty();

        public Builder(User user, String itemName) {
            this.user = user;
            this.itemName = itemName;
        }

        public Builder setItemPictures(List<ItemPicture> itemPictures) {
            this.itemPictures = Optional.of(itemPictures);
            return this;
        }

        public Builder setCost(Float cost) {
            this.cost = Optional.of(cost);
            return this;
        }

        public Builder setManufacturer(Manufacturer manufacturer) {
            this.manufacturer = Optional.of(manufacturer);
            return this;
        }

        public Builder setModelName(String modelName) {
            this.modelName = Optional.of(modelName);
            return this;
        }

        public Builder setColorHex(String colorHex) {
            this.colorHex = Optional.of(colorHex);
            return this;
        }

        public Builder setSerialNumber(String serialNumber) {
            this.serialNumber = Optional.of(serialNumber);
            return this;
        }

        public NonScannableItem build() {
            return new NonScannableItem(this);
        }
    }
}
