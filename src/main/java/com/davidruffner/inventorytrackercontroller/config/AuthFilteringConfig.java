package com.davidruffner.inventorytrackercontroller.config;

import com.davidruffner.inventorytrackercontroller.exceptions.ControllerException;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.INTERNAL_ERROR;

@Component
@ConfigurationProperties(prefix = "auth-filtering")
public class AuthFilteringConfig {
    private List<IPAddressString> allowedIPAddressStrings = new ArrayList<>();
    private List<String> adminEndpoints = new ArrayList<>();

    public void setAllowedIPAddresses(List<String> allowedIPAddresses) throws ControllerException {
        for (String addStr : allowedIPAddresses) {
            try {
                IPAddressString addressString = new IPAddressString(addStr);
                addressString.validate();
                this.allowedIPAddressStrings.add(addressString);
            } catch (AddressStringException ex) {
                throw new ControllerException.Builder(INTERNAL_ERROR, this.getClass())
                        .withErrorMessage(String.format("IP address '%s' in config is" +
                                " invalid IP address", addStr))
                        .build();
            }
        }
    }

    public void setAdminEndpoints(List<String> adminEndpoints) {
        this.adminEndpoints = adminEndpoints;
    }

    public boolean isAddressAuthorized(String givenAddress) throws ControllerException {
        IPAddressString givenAddrStr = new IPAddressString(givenAddress);
        try {
            givenAddrStr.validate();
        } catch (AddressStringException ex) {
            throw new ControllerException.Builder(BAD_REQUEST, this.getClass())
                    .withResponseMessage(String.format("IP address '%s' is not " +
                            "a valid IPv4 or IPv6 address or address range", givenAddress))
                    .build();
        }

        for (IPAddressString addrStr : this.allowedIPAddressStrings) {
            if (addrStr.contains(givenAddrStr)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEndpointAdmin(String endpoint) {
        return this.adminEndpoints.contains(endpoint);
    }
}
