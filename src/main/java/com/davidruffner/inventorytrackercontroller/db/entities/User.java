package com.davidruffner.inventorytrackercontroller.db.entities;

import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @Column(name = "User_ID")
    private String userId;

    @Column(name = "Secret")
    private String secret;

    @Column(name = "First_Name")
    private String firstName;

    @Column(name = "Last_Name")
    private String lastName;

//    @ManyToMany(fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, DETACH, REFRESH})
//    @JoinTable(
//            name = "AuthorizedUserDevice",
//            joinColumns = @JoinColumn(name = "User_ID"),
//            inverseJoinColumns = @JoinColumn(name = "Device_ID")
//    )
    @ManyToMany(cascade = ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "AuthorizedUserDevice",
            joinColumns = {
                    @JoinColumn(name = "User_ID")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "Device_ID")
            }
    )
    private List<Device> authorizedDevices = new ArrayList<>();

    public User() {}

    public User(String userId, String secret, String firstName, String lastName) {
        this.userId = userId;
        this.secret = secret;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Device> getAuthorizedDevices() {
        return authorizedDevices;
    }

    public void addAuthorizedDevice(Device device) {
        if (!this.authorizedDevices.contains(device))
            this.authorizedDevices.add(device);

    }

    public void removeAuthorizedDevice(Device device) {
        this.authorizedDevices.remove(device);
    }
}
