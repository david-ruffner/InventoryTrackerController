package com.davidruffner.inventorytrackercontroller.db;

import com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress;
import com.davidruffner.inventorytrackercontroller.db.repositories.AllowedIPAddressRepository;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress.IPVersion.IPV4;

@SpringBootTest
@ActiveProfiles("local")
public class AllowedIPAddressServiceTest {
    @Autowired
    AllowedIPAddressRepository repo;

    @Test
    public void test() throws ControllerException {
        AllowedIPAddress address = new AllowedIPAddress("192.168.0.0/24", IPV4);
        repo.save(address);

        repo.getAllowedIPAddressRanges().get().forEach(add -> {
            System.out.printf("\nAllowed range: %s", add.getIpv4Address());
        });
    }
}
