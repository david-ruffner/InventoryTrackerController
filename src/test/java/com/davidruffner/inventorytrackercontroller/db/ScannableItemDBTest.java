package com.davidruffner.inventorytrackercontroller.db;

import com.davidruffner.inventorytrackercontroller.db.entities.ScannableItem;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.ScannableItemRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ScannableItemDBTest {
    @Autowired
    ScannableItemRepository scannableItemRepo;

    @Autowired
    UserRepository userRepo;

    @Test
    public void testAddScannableItem() {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        user.addScannableItem(
                new ScannableItem.Builder(user, itemName,
                        mainBarcode, 10, 3)
                        .setAltBarcode(altBarcode)
                        .setCost(10.88f)
                        .build()
                );
        userRepo.save(user);

        ScannableItem item = scannableItemRepo.getScannableItemByMainBarcode(mainBarcode);
        assertEquals(mainBarcode, item.getMainBarcode());
        assertEquals(altBarcode, item.getAltBarcode());
        assertEquals(itemName, item.getItemName());
        assertEquals(user.getUserId(), item.getUser().getUserId());
        assertEquals(10, item.getCurrentCount());
        assertEquals(3, item.getThreshold());
        assertEquals(10.88f, item.getCost());
    }
}
