package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "Devices")
public class Device {
    @Id
    @Column(name = "Device_ID")
    private String deviceId;

    @Column(name = "Device_Name")
    private String deviceName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "AuthorizedUserDevice",
            joinColumns = {
                    @JoinColumn(name = "Device_ID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "User_ID")
            }
    )
    private List<User> authorizedUsers = new ArrayList<>();

    public Device() {}

    public Device(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<User> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void addAuthorizedUser(User user) {
        if (!this.authorizedUsers.contains(user))
            this.authorizedUsers.add(user);
    }

    public void removeAuthorizedUser(User user) {
        this.authorizedUsers.remove(user);
    }
}
