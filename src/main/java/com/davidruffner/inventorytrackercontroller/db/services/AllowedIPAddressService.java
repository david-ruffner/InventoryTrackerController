package com.davidruffner.inventorytrackercontroller.db.services;

import com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress;
import com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress.IPVersion;
import com.davidruffner.inventorytrackercontroller.db.repositories.AllowedIPAddressRepository;
import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;


@Service
public class AllowedIPAddressService {
    @Autowired
    AllowedIPAddressRepository addressRepo;

    public void addAllowedIPAddresses(List<String> ipAddresses, IPVersion ipVersion) {
        ipAddresses.forEach(add -> {
            AllowedIPAddress address = new AllowedIPAddress(add, ipVersion);
            addressRepo.save(address);
        });
    }

    public boolean isIPAddressAllowed(String ipAddress) throws ControllerException {
        //TODO: Need to use the new IPAddress validation library to simplify things
        InetAddressValidator validator = InetAddressValidator.getInstance();
        String validAddress;

        if (validator.isValidInet4Address(ipAddress)) {
            validAddress = ipAddress;
        } else if (validator.isValidInet6Address(ipAddress)) {
            validAddress = ipAddress;
        }
        else {
            throw new ControllerException.Builder(BAD_REQUEST, this.getClass())
                    .withResponseMessage(String.format("IP Address: %s is not " +
                            "a valid IPv4 or IPv6 Address", ipAddress))
                    .build();
        }

        return addressRepo.getAllowedIPAddress(validAddress).isPresent();
    }
}
