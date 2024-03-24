package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "ItemPictures")
public class ItemPicture {
    @Id
    @Column(name = "Item_Picture_ID", nullable = false)
    private String itemPictureId;

    @Column(name = "File_Extension", nullable = false)
    private String fileExtension;

    @Column(name = "Name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "Item_ID", nullable = false)
    private NonScannableItem nonScannableItem;

    public ItemPicture() {}

    public ItemPicture(String fileExtension) {
        this.itemPictureId = UUID.randomUUID().toString();
        this.fileExtension = fileExtension;
    }

    public ItemPicture(String fileExtension, String name) {
        this.itemPictureId = UUID.randomUUID().toString();
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemPictureId() {
        return itemPictureId;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }
}
