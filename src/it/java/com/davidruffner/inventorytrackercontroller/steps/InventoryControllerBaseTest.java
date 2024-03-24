package com.davidruffner.inventorytrackercontroller.steps;

import com.davidruffner.inventorytrackercontroller.FunctionalTestConfig;
import com.davidruffner.inventorytrackercontroller.config.AuthFilteringConfig;
import com.davidruffner.inventorytrackercontroller.db.repositories.DeviceRepository;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class InventoryControllerBaseTest {
    @Value("http://localhost:${server.port:8080}")
    protected String hostURL;

    @Autowired
    private FunctionalTestConfig functionalTestConfig;

    @Autowired
    protected AuthFilteringConfig authFilteringConfig;

    @Autowired
    protected UserRepository userRepo;

    @Autowired
    protected DeviceRepository deviceRepo;

    @Autowired
    protected ApplicationContext appContext;

    protected String getEndpointURL(String endpoint) {
        return String.format("%s%s", hostURL, endpoint);
    }
}
