package com.davidruffner.inventorytrackercontroller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "endpoint")
@ConfigurationPropertiesScan
public class EndpointConfig {
    private final List<String> adminEndpoints;


    public EndpointConfig(List<String> adminEndpoints) {
        this.adminEndpoints = adminEndpoints;
    }

    public List<String> getAdminEndpoints() {
        return adminEndpoints;
    }

    // Checks if a given endpoint is only allowed access by admins
    public boolean isEndpointAdmin(String endpoint) {
        return this.adminEndpoints.contains(endpoint);
    }
}
