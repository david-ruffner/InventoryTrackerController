package com.davidruffner.inventorytrackercontroller.db;

import com.davidruffner.inventorytrackercontroller.db.entities.Device;
import com.davidruffner.inventorytrackercontroller.db.entities.ScannableItem;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.ScannableItemRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import com.davidruffner.inventorytrackercontroller.db.services.ScannableItemService;
import com.davidruffner.inventorytrackercontroller.db.services.UserService;
import com.davidruffner.inventorytrackercontroller.exceptions.ItemException;
import com.davidruffner.inventorytrackercontroller.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.davidruffner.inventorytrackercontroller.util.Constants.MAX_ITEM_COUNT;
import static com.davidruffner.inventorytrackercontroller.util.Constants.MIN_ITEM_COUNT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
public class ScannableItemDBTest {
    @Autowired
    ScannableItemRepository scannableItemRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserService userService;

    @Autowired
    ScannableItemService scannableItemService;

    User user;

    @BeforeEach
    public void setUp() {
        user = new User("davidruffner@icloud.com", "password", "David",
                "Ruffner", true);
        userRepo.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepo.deleteAll();
        scannableItemRepo.deleteAll();
    }

    @Test
    public void testAddScannableItem() {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        user.addScannableItem(new ScannableItem.Builder(user, itemName,
                mainBarcode, 10, 3)
                .setAltBarcode(altBarcode)
                .setCost(10.88f)
                .build());
        userRepo.save(user);

        ScannableItem actualItem = scannableItemRepo.getScannableItemByMainBarcode(mainBarcode).get();
        assertEquals(mainBarcode, actualItem.getMainBarcode());
        assertEquals(altBarcode, actualItem.getAltBarcode());
        assertEquals(itemName, actualItem.getItemName());
        assertEquals(user.getUserId(), actualItem.getUser().getUserId());
        assertEquals(10, actualItem.getCurrentCount());
        assertEquals(3, actualItem.getThreshold());
        assertEquals(10.88f, actualItem.getCost());
    }

    @Test
    public void testRemoveScannableItem() {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        ScannableItem item = new ScannableItem.Builder(user, itemName,
                mainBarcode, 10, 3)
                .setAltBarcode(altBarcode)
                .setCost(10.88f)
                .build();
        user.addScannableItem(item);
        userRepo.save(user);

        scannableItemService.removeScannableItem(user, item);

        User actualUser = userRepo.findById("davidruffner@icloud.com").get();
        assertTrue(actualUser.getScannableItems().isEmpty());
        assertFalse(scannableItemRepo.findAll().iterator().hasNext());
    }

    @Test
    public void testIncrementItemCount_Success() throws Exception {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        ScannableItem item = new ScannableItem.Builder(user, itemName,
                mainBarcode, 10, 3)
                .setAltBarcode(altBarcode)
                .setCost(10.88f)
                .build();
        user.addScannableItem(item);
        userRepo.save(user);

        scannableItemService.increaseItemCount(item);
        User actualUser = userRepo.findById("davidruffner@icloud.com").get();
        assertEquals(11, actualUser.getScannableItems().getFirst().getCurrentCount());
    }

    @Test
    public void testIncrementItemCount_Failure() {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        ScannableItem item = new ScannableItem.Builder(user, itemName,
                mainBarcode, MAX_ITEM_COUNT, 3)
                .setAltBarcode(altBarcode)
                .setCost(10.88f)
                .build();
        user.addScannableItem(item);
        userRepo.save(user);

        assertThrows(ItemException.class, () -> {
            scannableItemService.increaseItemCount(item);
        });
    }

    @Test
    public void testDecrementItemCount_Success() throws Exception {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        ScannableItem item = new ScannableItem.Builder(user, itemName,
                mainBarcode, 10, 3)
                .setAltBarcode(altBarcode)
                .setCost(10.88f)
                .build();
        user.addScannableItem(item);
        userRepo.save(user);

        scannableItemService.decreaseItemCount(item);
        User actualUser = userRepo.findById("davidruffner@icloud.com").get();
        assertEquals(9, actualUser.getScannableItems().getFirst().getCurrentCount());
    }

    @Test
    public void testDecrementItemCount_Failure() {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        ScannableItem item = new ScannableItem.Builder(user, itemName,
                mainBarcode, MIN_ITEM_COUNT, 3)
                .setAltBarcode(altBarcode)
                .setCost(10.88f)
                .build();
        user.addScannableItem(item);
        userRepo.save(user);

        assertThrows(ItemException.class, () -> {
            scannableItemService.decreaseItemCount(item);
        });
    }

    @Test
    public void testIsItemCountAtThreshold() {
        String mainBarcode = "82JFH2FJD82FJS28F";
        String altBarcode = "FJS8JFJ8S";
        String itemName = "Donut House K-Cups";

        User user = userRepo.findById("davidruffner@icloud.com").get();
        ScannableItem item = new ScannableItem.Builder(user, itemName,
                mainBarcode, 3, 3)
                .setAltBarcode(altBarcode)
                .setCost(10.88f)
                .build();
        user.addScannableItem(item);
        userRepo.save(user);

        User actualUser = userRepo.findById("davidruffner@icloud.com").get();
        assertTrue(scannableItemService.isItemCountAtThreshold(
                actualUser.getScannableItems().getFirst()));
    }
}
