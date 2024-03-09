package com.davidruffner.inventorytrackercontroller.db.repositories;

import com.davidruffner.inventorytrackercontroller.db.entities.NonScannableItem;
import com.davidruffner.inventorytrackercontroller.db.entities.ScannableItem;
import org.springframework.data.repository.CrudRepository;

public interface NonScannableItemRepository extends CrudRepository<NonScannableItem, String> {
}
