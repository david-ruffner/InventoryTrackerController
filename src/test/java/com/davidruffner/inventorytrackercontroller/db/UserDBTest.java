package com.davidruffner.inventorytrackercontroller.db;

import com.davidruffner.inventorytrackercontroller.db.entities.Device;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.DeviceRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
public class UserDBTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User("test@test.com", "secret",
                "David", "Ruffner", true);
        user.addAuthorizedDevice(new Device("fuf832fu2", "Kitchen Scanner"));
        user.addAuthorizedDevice(new Device("f2fj822f", "Bathroom Scanner"));
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteById("test@test.com");
    }

    @Test
    public void testUsers() {
        User user = userRepository.findById("test@test.com").orElseThrow();
        assertEquals("David", user.getFirstName());
        assertEquals("Ruffner", user.getLastName());
        assertTrue(user.isAdmin());
        assertFalse(user.getAuthorizedDevices().isEmpty());
    }
}
