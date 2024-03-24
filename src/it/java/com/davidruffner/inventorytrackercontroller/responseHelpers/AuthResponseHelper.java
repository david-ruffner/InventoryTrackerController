package com.davidruffner.inventorytrackercontroller.responseHelpers;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseHelper extends ResponseHelperBase {
    private final String token;
    private final String displayName;

    public AuthResponseHelper(@JsonProperty("responseStatus") String responseStatus,
                              @JsonProperty("token") String token,
                              @JsonProperty("displayName") String displayName) {
        super(responseStatus);
        this.token = token;
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName() {
        return displayName;
    }
}
