package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Manufacturers")
public class Manufacturer {
    @Id
    @Column(name = "Manufacturer_ID", nullable = false)
    private String manufacturerId;

    @Column(name = "Name", nullable = false)
    private String name;

    @ManyToOne()
    @JoinColumn(name = "User_ID")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NonScannableItem> nonScannableItems = new ArrayList<>();

    public Manufacturer() {}

    public Manufacturer(User user, String name) {
        this.manufacturerId = UUID.randomUUID().toString();
        this.user = user;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<NonScannableItem> getNonScannableItems() {
        return nonScannableItems;
    }
}
