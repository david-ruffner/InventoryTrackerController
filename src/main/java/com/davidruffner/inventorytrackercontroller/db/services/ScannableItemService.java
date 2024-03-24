package com.davidruffner.inventorytrackercontroller.db.services;

import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus;
import com.davidruffner.inventorytrackercontroller.db.entities.ScannableItem;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.ScannableItemRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import com.davidruffner.inventorytrackercontroller.util.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.davidruffner.inventorytrackercontroller.util.Constants.MAX_ITEM_COUNT;
import static com.davidruffner.inventorytrackercontroller.util.Constants.MIN_ITEM_COUNT;

@Service
public class ScannableItemService {
    private final Logging LOGGER = new Logging(this.getClass());

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

    public void increaseItemCount(ScannableItem item) throws ControllerException {
        if (item.getCurrentCount() < MAX_ITEM_COUNT) {
            item.incrementCurrentCount();
            scannableItemRepo.save(item);
        } else {
            throw new ControllerException.Builder(ResponseStatus.ResponseStatusCode.INTERNAL_ERROR, this.getClass())
                    .withResponseMessage(String.format("Item ID: %s cannot be increased past %d",
                            item.getItemId(), MAX_ITEM_COUNT))
                    // TODO: Fix this
//                    .withUserId(item.getUser().getUserId())
                    .withUserId("")
                    .build();
        }
    }

    public void decreaseItemCount(ScannableItem item) throws ControllerException {
        if (item.getCurrentCount() > MIN_ITEM_COUNT) {
            item.decrementCurrentCount();
            scannableItemRepo.save(item);
        } else {
            throw new ControllerException.Builder(ResponseStatus.ResponseStatusCode.INTERNAL_ERROR, this.getClass())
                    .withResponseMessage(String.format("Item ID: %s cannot be decreased past %d",
                            item.getItemId(), MIN_ITEM_COUNT))
//                    .withUserId(item.getUser().getUserId()) // TODO: Fix this
                    .withUserId("")
                    .build();
        }
    }

    public Boolean isItemCountAtThreshold(ScannableItem item) {
        return item.getCurrentCount() <= item.getThreshold();
    }
}
