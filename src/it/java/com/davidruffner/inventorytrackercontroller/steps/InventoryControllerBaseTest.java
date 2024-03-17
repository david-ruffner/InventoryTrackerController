package com.davidruffner.inventorytrackercontroller.steps;

import com.davidruffner.inventorytrackercontroller.db.repositories.AllowedIPAddressRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.DeviceRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class InventoryControllerBaseTest {
    @Autowired
    protected AllowedIPAddressRepository allowedIPAddressRepo;

    @Autowired
    protected UserRepository userRepo;

    @Autowired
    protected DeviceRepository deviceRepo;

    @Value("${server.port}")
    protected int serverPort;
}
