package com.davidruffner.inventorytrackercontroller.db.services;

import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.db.repositories.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    public Optional<User> getUser(String username, String password) {
        Optional<User> user = userRepo.findById(username);
        if (user.isEmpty())
            return user;

        if (!DigestUtils.sha256Hex(password).equals(user.get().getSecret()))
            return Optional.empty();

        return user;
    }

    public boolean isUserDeviceAuthorized(User user, String device_id) {
        return user.getAuthorizedDevices().stream().anyMatch(device ->
                device.getDeviceId().equals(device_id));
    }

    public boolean isFirstNameUnique(String firstName) {
        return userRepo.getFirstNameCount(firstName) < 2;
    }
}
