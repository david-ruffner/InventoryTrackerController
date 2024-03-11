package com.davidruffner.inventorytrackercontroller.db.repositories;

import com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AllowedIPAddressRepository extends CrudRepository<AllowedIPAddress, String> {
    @Query(
            value = """
                select ip
                from AllowedIPAddress ip
                where ip.ipv4Address = :ipAddress or ip.ipv6Address = :ipAddress
            """
    )
    Optional<AllowedIPAddress> getAllowedIPAddress(String ipAddress);
}
