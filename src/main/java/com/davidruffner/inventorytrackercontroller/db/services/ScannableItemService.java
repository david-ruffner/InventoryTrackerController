package com.davidruffner.inventorytrackercontroller.db.services;

import com.davidruffner.inventorytrackercontroller.db.entities.ScannableItem;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.ScannableItemRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import com.davidruffner.inventorytrackercontroller.exceptions.ItemException;
import com.davidruffner.inventorytrackercontroller.util.Constants;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.davidruffner.inventorytrackercontroller.util.Constants.MAX_ITEM_COUNT;
import static com.davidruffner.inventorytrackercontroller.util.Constants.MIN_ITEM_COUNT;

@Service
public class ScannableItemService {
    //    private static final Logger LOGGER = LoggerFactory.getLogger(ScannableItemService.class);
//    private final Logging LOGGER = new Logging(this.getClass());

    @Autowired
    UserRepository userRepo;

    @Autowired
    ScannableItemRepository scannableItemRepo;

    public void addScannableItem(User user, ScannableItem item) {
        scannableItemRepo.save(item);
        user.addScannableItem(item);
        userRepo.save(user);
    }

    public void removeScannableItem(User user, ScannableItem item) {
        user.removeScannableItem(item);
        scannableItemRepo.delete(item);
        userRepo.save(user);
    }

    public void increaseItemCount(ScannableItem item) throws ItemException {
        if (item.getCurrentCount() < MAX_ITEM_COUNT) {
            item.incrementCurrentCount();
            scannableItemRepo.save(item);
        } else {
            throw new ItemException(item.getUser().getUserId(), String.format(
                    "Item ID: %s cannot be incremented past the " +
                            "max item count of %d", item.getItemId(), MAX_ITEM_COUNT),
                    this.getClass());
        }
    }

    public void decreaseItemCount(ScannableItem item) throws ItemException {
        if (item.getCurrentCount() > MIN_ITEM_COUNT) {
            item.decrementCurrentCount();
            scannableItemRepo.save(item);
        } else {
            throw new ItemException(item.getUser().getUserId(),
                    String.format("Item ID: %s cannot be decremented past the " +
                            "minimum item count of %d", item.getItemId(), MIN_ITEM_COUNT),
                    this.getClass());
        }
    }

    public Boolean isItemCountAtThreshold(ScannableItem item) {
        return item.getCurrentCount() <= item.getThreshold();
    }
}
