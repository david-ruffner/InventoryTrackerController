package com.davidruffner.inventorytrackercontroller.db;

import com.davidruffner.inventorytrackercontroller.db.entities.Device;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.DeviceRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("local")
public class DeviceDBTest {
    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User("test@test.com", "secret",
                "David", "Ruffner", true);
        userRepository.save(user);
        Device device = new Device("fuf832fu2", "Kitchen Scanner");
        device.addAuthorizedUser(user);
        deviceRepository.save(device);
    }

    @Test
    public void testUsers() {
        Device device = deviceRepository.findById("fuf832fu2").orElseThrow();
        assertEquals("Kitchen Scanner", device.getDeviceName());
        assertFalse(device.getAuthorizedUsers().isEmpty());

        deviceRepository.deleteById("fuf832fu2");
        userRepository.findById("test@test.com").orElseThrow();
    }
}
