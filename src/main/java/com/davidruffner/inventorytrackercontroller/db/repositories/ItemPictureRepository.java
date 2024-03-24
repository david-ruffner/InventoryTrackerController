package com.davidruffner.inventorytrackercontroller.db.repositories;

import com.davidruffner.inventorytrackercontroller.db.entities.ItemPicture;
import org.springframework.data.repository.CrudRepository;

public interface ItemPictureRepository extends CrudRepository<ItemPicture, String> {
}
